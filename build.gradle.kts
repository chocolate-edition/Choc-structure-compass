plugins {
    id("dev.prism")
}

group = "com.leclowndu93150"
version = "1.0.2"

prism {
    metadata {
        modId = "structurecompassfix"
        name = "Structure Compass Fix"
        description = "A Minecraft mod."
        license = "MIT"
    }

    version("1.20.1") {
        forge {
            loaderVersion = "47.4.18"
            loaderVersionRange = "[47,)"
        }

    }

    version("1.19.2") {
        forge {
            loaderVersion = "43.4.4"
            loaderVersionRange = "[43,)"
        }
    }

}
