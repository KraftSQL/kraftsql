val testAgent = configurations.create("testAgent")

tasks.withType(Test::class) {
    jvmArgumentProviders.add(object : CommandLineArgumentProvider {
        @get:InputFiles @get:Classpath val agent : FileCollection = testAgent
        override fun asArguments(): Iterable<String> = listOf("-javaagent:${agent.asPath}")
    })
}
