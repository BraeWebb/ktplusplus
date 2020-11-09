java_library(
    name = "ktplusplus_library",
    srcs = glob(["src/**/*.java"]),
    deps = [
        "@maven//:com_puppycrawl_tools_checkstyle",
        "@maven//:com_esotericsoftware_yamlbeans_yamlbeans",
    ],
)

java_binary(
    name = "ktplusplus",
    runtime_deps = [":ktplusplus_library"],
    main_class = "ktplusplus.Main"
)
