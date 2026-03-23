package rocks.frieler.kraftsql.testing.simulator.engine

import rocks.frieler.kraftsql.ddl.CreateTable
import rocks.frieler.kraftsql.ddl.DropTable
import rocks.frieler.kraftsql.dml.BeginTransaction
import rocks.frieler.kraftsql.dml.CommitTransaction
import rocks.frieler.kraftsql.dml.Delete
import rocks.frieler.kraftsql.dml.InsertInto
import rocks.frieler.kraftsql.dml.RollbackTransaction
import rocks.frieler.kraftsql.engine.Engine
import rocks.frieler.kraftsql.objects.ConstantData
import rocks.frieler.kraftsql.objects.DataRow
import rocks.frieler.kraftsql.objects.Table
import rocks.frieler.kraftsql.dql.Select
import kotlin.reflect.KClass

/**
 * Generic [SimulatorConnection], that implements common behavior of SQL engines and dialects for testing.
 *
 * The [GenericSimulatorConnection] is configurable and extendable to simulate specific SQL engines and dialects.
 *
 * @param E the [Engine] to simulate
 * @param engine the [EngineSimulator] to "connect" to, defaults to a [GenericEngineSimulator]
 * @param orm the [SimulatorORMapping] to use, defaults to the generic [SimulatorORMapping]
 */
open class GenericSimulatorConnection<E : Engine<E>>(
    protected val engine: EngineSimulator<E> = GenericEngineSimulator(),
    private val orm: SimulatorORMapping<E> = SimulatorORMapping(),
) : SimulatorConnection<E> {
    protected var topState:  EngineState<E> = engine.persistentState

    override fun <T : Any> execute(select: Select<E, T>, type: KClass<T>): List<T> {
        val resultRows = context(topState) { engine.queryEvaluator.selectRows(select, null) }
        return orm.deserializeQueryResult(resultRows, type)
    }

    override fun execute(createTable: CreateTable<E>) {
        commitAllOpenTransactions()
        check(!engine.persistentState.containsTable(createTable.table.qualifiedName)) { "Table '${createTable.table.qualifiedName}' already exists." }
        engine.persistentState.addTable(createTable.table)
    }

    override fun execute(dropTable: DropTable<E>) {
        commitAllOpenTransactions()
        if (!dropTable.ifExists) {
            check(engine.persistentState.containsTable(dropTable.table.qualifiedName)) { "Table '${dropTable.table.qualifiedName}' does not exist." }
        }
        engine.persistentState.removeTable(dropTable.table)
    }

    override fun execute(insertInto: InsertInto<E, *>): Int {
        val table = topState
            .also { (it as? TransactionStateOverlay<E>)?.ensureTableCopy(insertInto.table.qualifiedName) }
            .getTable(insertInto.table.qualifiedName)
        val rows = insertInto.values.let { values ->
            when (values) {
                is ConstantData -> values.items.map { item -> engine.expressionEvaluator.simulateExpression(orm.serialize(item)).invoke(DataRow()) as DataRow }
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

    override fun execute(delete: Delete<E>): Int {
        val table = topState
            .also { (it as? TransactionStateOverlay<E>)?.ensureTableCopy(delete.table.qualifiedName) }
            .getTable(delete.table.qualifiedName)
        val tableSizeBefore = table.second.size
        val condition = delete.condition?.let { engine.expressionEvaluator.simulateExpression(it) }
        if (condition == null) {
            table.second.clear()
        } else {
            table.second.removeAll { row -> condition.invoke(row) ?: false }
        }
        return tableSizeBefore - table.second.size
    }

    override fun execute(beginTransaction: BeginTransaction<E>) {
        topState = TransactionStateOverlay(topState)
    }

    override fun execute(commitTransaction: CommitTransaction<E>) {
        topState = topState.let {
            if (it is TransactionStateOverlay<E>) {
                it.commitIntoParent()
            } else {
                error("No open transaction to commit.")
            }
        }
    }

    private fun commitAllOpenTransactions() {
        while (topState is TransactionStateOverlay<E>) execute(CommitTransaction())
    }

    override fun execute(rollbackTransaction: RollbackTransaction<E>) {
        topState = topState.let {
            if (it is TransactionStateOverlay<E>) {
                it.parent
            } else {
                error("No open transaction to roll back.")
            }
        }
    }

}
