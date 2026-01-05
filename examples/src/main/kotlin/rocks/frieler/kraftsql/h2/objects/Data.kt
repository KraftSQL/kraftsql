package rocks.frieler.kraftsql.h2.objects

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.collect

typealias Data<T> = Data<H2Engine, T>

inline fun <reified T : Any> rocks.frieler.kraftsql.h2.objects.Data<T>.collect() =
    collect(H2Engine.DefaultConnection.get())
