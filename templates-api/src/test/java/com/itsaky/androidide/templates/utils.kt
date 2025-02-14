/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.itsaky.androidide.templates

import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.itsaky.androidide.managers.PreferenceManager
import com.itsaky.androidide.preferences.internal.prefManager
import com.itsaky.androidide.templates.base.ProjectTemplateConfigurator
import com.itsaky.androidide.templates.base.baseProject
import com.itsaky.androidide.utils.Environment
import com.itsaky.androidide.utils.FileProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import java.io.File
import kotlin.reflect.KClass

val testProjectsDir: File by lazy {
  FileProvider.currentDir().resolve("build/templateTest").toFile()
}

fun mockPrefManager(configure: PreferenceManager.() -> Unit = {}) {
  mockkStatic(::prefManager)

  val manager = PreferenceManager(ApplicationProvider.getApplicationContext())
  every { prefManager } answers { manager }
  manager.configure()
}

fun testTemplate(block: ProjectTemplateConfigurator): Template {
  mockPrefManager()
  testProjectsDir.apply {
    if (exists()) {
      delete()
    }
    mkdirs()
  }

  Environment.PROJECTS_DIR = testProjectsDir

  return baseProject {
    templateName = -123
    thumb = -123
    block()
  }.also {
    testProjectsDir.delete()
  }
}

fun Template.setupRootProjectParams() {
  val iterator = parameters.iterator()

  // name
  var param = iterator.next()
  (param as StringParameter).value = "TestTemplate"

  // package
  param = iterator.next()
  (param as StringParameter).value = "com.itsaky.androidide.template"

  // save location
  param = iterator.next()
  (param as StringParameter).value = testProjectsDir.absolutePath

  // language
  param = iterator.next()
  (param as EnumParameter<Language>).value = Language.Kotlin

  // Min SDK
  param = iterator.next()
  (param as EnumParameter<Sdk>).value = Sdk.Lollipop
}

fun Template.executeRecipe() {
  TestRecipeExecutor().apply(recipe)
}

fun Collection<Parameter<*>>.assertParameterTypes(checker: (Int) -> KClass<out Parameter<*>>
) = assertTypes(checker)

fun Collection<Widget<*>>.assertWidgetTypes(checker: (Int) -> KClass<out Widget<*>>
) = assertTypes(checker)


fun <T : Any> Collection<T>.assertTypes(checker: (Int) -> KClass<out T>) {
  forEachIndexed { index, element ->
    assertThat(element).isInstanceOf(checker(index).java)
  }
}