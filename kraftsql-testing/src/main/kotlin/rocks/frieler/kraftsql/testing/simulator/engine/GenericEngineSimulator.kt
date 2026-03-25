package rocks.frieler.kraftsql.testing.simulator.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.ddl.DropTable
import rocks.frieler.kraftsql.dml.BeginTransaction
import rocks.frieler.kraftsql.dml.CommitTransaction
import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.dml.RollbackTransaction
import rocks.frieler.kraftsql.dql.Select
import rocks.frieler.kraftsql.engine.Connection
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.testing.simulator.expressions.GenericExpressionEvaluator
import java.util.WeakHashMap
import kotlin.reflect.KClass

/**
 * Generic configurable and extendable [EngineSimulator], implementing the core functionality of a basic SQL [Engine].
 *
 * @param E the [Engine] to simulate
 * @param orm a [SimulatorORMapping] to convert between Kotlin types and [DataRow]s
 * @param persistentState the [EngineState] that holds the persistent data
 * @param expressionEvaluator the [GenericExpressionEvaluator] to evaluate expressions as supported by the [Engine]
 * @param queryEvaluator the [GenericQueryEvaluator] to evaluate queries as supported by the [Engine]
 */
open class GenericEngineSimulator<E : Engine<E>>(
    private val orm: SimulatorORMapping<E> = SimulatorORMapping(),
    protected open val persistentState: EngineState<E> = EngineState(),
    protected open val expressionEvaluator: GenericExpressionEvaluator<E> = GenericExpressionEvaluator(),
    protected open val queryEvaluator: GenericQueryEvaluator<E> = GenericQueryEvaluator(expressionEvaluator = expressionEvaluator),
) : EngineSimulator<E> {
    init {
        require(queryEvaluator.expressionEvaluatorForChecking == expressionEvaluator) { "QueryEvaluator must use no other than the Engine-wide ExpressionEvaluator." }
    }

    context(connection: Connection<E>)
    override fun <T : Any> execute(select: Select<E, T>, type: KClass<T>): List<T> {
        val resultRows = context(getTopState(connection)) {
            queryEvaluator.selectRows(select, null)
        }
        return orm.deserializeQueryResult(resultRows, type)
    }

    context(connection: Connection<E>)
    override fun execute(createTable: CreateTable<E>) {
        commitAllOpenTransactions()
        check(!persistentState.containsTable(createTable.table.qualifiedName)) { "Table '${createTable.table.qualifiedName}' already exists." }
        persistentState.addTable(createTable.table)
    }

    context(connection: Connection<E>)
    override fun execute(dropTable: DropTable<E>) {
        commitAllOpenTransactions()
        if (!dropTable.ifExists) {
            check(persistentState.containsTable(dropTable.table.qualifiedName)) { "Table '${dropTable.table.qualifiedName}' does not exist." }
        }
        persistentState.removeTable(dropTable.table)
    }

    context(connection: Connection<E>)
    override fun execute(insertInto: InsertInto<E, *>): Int {
        val table = getTopState(connection)
            .also { (it as? TransactionStateOverlay<E>)?.ensureTableCopy(insertInto.table.qualifiedName) }
            .getTable(insertInto.table.qualifiedName)
        val rows = insertInto.values.let { values ->
            when (values) {
                is ConstantData -> values.items.map { item -> expressionEvaluator.simulateExpression(orm.serialize(item)).invoke(DataRow()) as DataRow }
                else -> throw NotImplementedError("Inserting ${values::class.qualifiedName} is not implemented.")
            }
        }
        rows.forEach { row ->
            require(row.columnNames == table.first.columns.map { column -> column.name }) {
                "$row to insert doesn't match table schema of '${table.first.qualifiedName}'."
            }
            require(table.first.columns.all { column -> column.nullable || row[column.name] != null }) {
                "$row to insert violates NOT NULL constraint of a column in '${table.first.qualifiedName}'."
            }
            table.second.add(row)
        }
        return rows.count()
    }

    context(connection: Connection<E>)
    override fun execute(delete: Delete<E>): Int {
        val table = getTopState(connection)
            .also { (it as? TransactionStateOverlay<E>)?.ensureTableCopy(delete.table.qualifiedName) }
            .getTable(delete.table.qualifiedName)
        val tableSizeBefore = table.second.size
        val condition = delete.condition?.let { expressionEvaluator.simulateExpression(it) }
        if (condition == null) {
            table.second.clear()
        } else {
            table.second.removeAll { row -> condition.invoke(row) ?: false }
        }
        return tableSizeBefore - table.second.size
    }

    protected val connectionStateOverlays = WeakHashMap<Connection<E>, EngineState<E>>()

    context(connection: Connection<E>)
    override fun execute(beginTransaction: BeginTransaction<E>) {
        connectionStateOverlays[connection] =
            TransactionStateOverlay(getTopState(connection))
    }

    context(connection: Connection<E>)
    override fun execute(commitTransaction: CommitTransaction<E>) {
        val transactionState = connectionStateOverlays[connection]
        check(transactionState is TransactionStateOverlay<E>) { "No open transaction to commit." }
        transactionState.commitIntoParent().also { parent ->
            if (parent == persistentState) {
                connectionStateOverlays.remove(connection)
            } else {
                connectionStateOverlays[connection] = parent
            }
        }
    }

    context(connection: Connection<E>)
    private fun commitAllOpenTransactions() {
        while (connectionStateOverlays[connection] is TransactionStateOverlay<E>) execute(CommitTransaction())
    }

    context(connection: Connection<E>)
    override fun execute(rollbackTransaction: RollbackTransaction<E>) {
        val transactionState = connectionStateOverlays[connection]
        check(transactionState is TransactionStateOverlay<E>) { "No open transaction to commit." }
        transactionState.parent.also { parent ->
            if (parent == persistentState) {
                connectionStateOverlays.remove(connection)
            } else {
                connectionStateOverlays[connection] = parent
            }
        }
    }

    protected open fun getTopState(connection: Connection<E>) : EngineState<E> =
        connectionStateOverlays.getOrDefault(connection, persistentState)
}
