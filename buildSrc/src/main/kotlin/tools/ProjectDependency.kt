package tools

import groovy.util.Node
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExcludeRule
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.kotlin.dsl.get

private const val scopeAPI = "api"

private val scopes = arrayOf(scopeAPI)

fun Project.handleProjectDependencies(
    xmlProvider: XmlProvider
) {
    val dependenciesNode = xmlProvider.asNode().appendNode("dependencies")
    scopes.forEach { scopeName ->
        handleScopes(
            dependenciesNode,
            configurations,
            scopeName
        )
    }
}

private fun handleScopes(
    dependenciesNode: Node?,
    configurations: ConfigurationContainer,
    scopeName: String
) {
    val configuration: Configuration? = configurations[scopeName]
    configuration?.allDependencies?.forEach {
        if (it is ModuleDependency) {
            if (it.isValidDependency()) {
                handleValidDependency(
                    it, dependenciesNode, configuration, scopeName
                )
            }
        }
    }
}

private fun Dependency?.isValidDependency() =
    this != null && group != null && version != null && name != "unspecified"

private fun handleValidDependency(
    dependency: Dependency,
    dependenciesNode: Node?,
    configuration: Configuration,
    scopeName: String
) {
    val dependencyNode = buildDependencyNode(dependency, dependenciesNode, scopeName)
    buildExclusionNode(configuration, dependencyNode)
}

private fun buildDependencyNode(
    dependency: Dependency,
    dependenciesNode: Node?,
    scope: String
): Node? {
    val dependencyNode = dependenciesNode?.appendNode("dependency")
    dependencyNode?.appendNode("groupId", dependency.group)
    dependencyNode?.appendNode("artifactId", dependency.name)
    dependencyNode?.appendNode("version", dependency.version)
    dependencyNode?.appendNode("scope", scope)
    return dependencyNode
}

private fun buildExclusionNode(configuration: Configuration?, dependencyNode: Node?) {
    if (configuration?.isTransitive == false) {
        buildExcludeTransitives(dependencyNode)
    } else if (configuration?.excludeRules?.isNotEmpty() == true) {
        buildExcludeRules(configuration, dependencyNode)
    }
}

private fun buildExcludeTransitives(dependencyNode: Node?) {
    val exclusionNode = dependencyNode?.appendNode("exclusions")?.appendNode("exclusion")
    exclusionNode?.appendNode("groupId", "*")
    exclusionNode?.appendNode("artifactId", "*")
}

private fun buildExcludeRules(configuration: Configuration?, dependencyNode: Node?) {
    val exclusionNode = dependencyNode?.appendNode("exclusions")?.appendNode("exclusion")
    configuration?.excludeRules?.forEach { rule: ExcludeRule? ->
        exclusionNode?.appendNode("groupId", rule?.group ?: "*")
        exclusionNode?.appendNode("artifactId", rule?.module ?: "*")
    }
}