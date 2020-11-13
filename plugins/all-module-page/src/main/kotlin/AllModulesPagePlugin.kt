package org.jetbrains.dokka.allModulesPage

import org.jetbrains.dokka.CoreExtensions
import org.jetbrains.dokka.allModulesPage.templates.*
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.transformers.pages.PageCreator
import org.jetbrains.dokka.transformers.pages.PageTransformer

class AllModulesPagePlugin : DokkaPlugin() {

    val templateProcessor by extensionPoint<TemplateProcessor>()
    val templateProcessingStrategy by extensionPoint<TemplateProcessingStrategy>()
    val allModulePageCreator by extensionPoint<PageCreator>()
    val allModulePageTransformer by extensionPoint<PageTransformer>()

    val substitutor by extensionPoint<Substitutor>()

    val allModulePageCreators by extending {
        allModulePageCreator providing ::MultimodulePageCreator
    }

    val multimoduleLocationProvider by extending {
        (plugin<DokkaBase>().locationProviderFactory
                providing MultimoduleLocationProvider::Factory
                override plugin<DokkaBase>().locationProvider
                applyIf { modules.size > 1 })
    }

    val allModulesPageGeneration by extending {
        (CoreExtensions.generation
                providing ::AllModulesPageGeneration
                override plugin<DokkaBase>().singleGeneration)
    }

    val defaultTemplateProcessor by extending {
        templateProcessor providing ::DefaultTemplateProcessor
    }

    val directiveBasedHtmlTemplateProcessingStrategy by extending {
        templateProcessingStrategy providing ::DirectiveBasedHtmlTemplateProcessingStrategy order {
            before(fallbackProcessingStrategy)
        }
    }

    val fallbackProcessingStrategy by extending {
        templateProcessingStrategy providing ::FallbackTemplateProcessingStrategy
    }

    val navigationSearchTemplateStrategy by extending {
        templateProcessingStrategy providing ::NavigationSearchTemplateStrategy order {
            before(fallbackProcessingStrategy)
        }
    }

    val pagesSearchTemplateStrategy by extending {
        templateProcessingStrategy providing ::PagesSearchTemplateStrategy order {
            before(fallbackProcessingStrategy)
        }
    }

    val pathToRootSubstitutor by extending {
        substitutor providing ::PathToRootSubstitutor
    }
}