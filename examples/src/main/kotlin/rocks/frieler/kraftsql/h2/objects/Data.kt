package rocks.frieler.kraftsql.h2.objects

import rocks.frieler.kraftsql.h2.engine.H2Engine
import rocks.frieler.kraftsql.objects.Data
import rocks.frieler.kraftsql.objects.collect


inline fun <reified T : Any> Data<H2Engine, T>.collect() = collect(H2Engine.DefaultConnection.get())
