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

import com.google.common.truth.Truth.assertThat
import com.itsaky.androidide.templates.base.modules.android.ManifestActivity
import com.itsaky.androidide.xml.permissions.Permission
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import jdkx.lang.model.element.Modifier
import jdkx.lang.model.element.Modifier.PUBLIC
import jdkx.lang.model.element.Modifier.STATIC
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Test template builder.
 *
 * @author Akash Yadav
 */

@RunWith(RobolectricTestRunner::class)
class TemplateBuilderTest {

  @Test
  fun `root project generator test`() {
    val template = testTemplate {

    }

    template.apply {

      assertThat(templateName).isEqualTo(-123)
      assertThat(thumb).isEqualTo(-123)
      assertThat(recipe).isNotNull()

      parameters.apply {
        assertThat(this).isNotEmpty()
        assertThat(this).hasSize(5)
        assertParameterTypes {
          when (it) {
            0 -> StringParameter::class
            1 -> StringParameter::class
            2 -> StringParameter::class
            3 -> EnumParameter::class
            4 -> EnumParameter::class
            else -> throw IndexOutOfBoundsException("index $it")
          }
        }
      }

      widgets.apply {
        assertThat(this).isNotEmpty()
        assertThat(this).hasSize(5)
        assertWidgetTypes {
          when (it) {
            0 -> TextFieldWidget::class
            1 -> TextFieldWidget::class
            2 -> TextFieldWidget::class
            3 -> SpinnerWidget::class
            4 -> SpinnerWidget::class
            else -> throw IndexOutOfBoundsException("index $it")
          }
        }
      }
    }

    template.setupRootProjectParams()
    template.executeRecipe()
  }

  @Test
  fun `test project with module`() {
    val template = testTemplate {
      defaultModule {
        manifest {
          addPermission(Permission.INTERNET)
          addActivity(ManifestActivity(name = ".MainActivity", isExported = true, isLauncher = true))

          java {
            newClass("com.itsaky", "TestClass") {
              MethodSpec.methodBuilder("main").apply {
                addModifiers(PUBLIC, STATIC)
                returns(TypeName.VOID)
                addParameter(ParameterSpec.builder(ArrayTypeName.get(java.lang.String::class.java), "args").build())
                addStatement("System.out.println(\"Hello mofo!\")")
              }.build().also { addMethod(it) }
            }
          }
        }
      }
    }

    template.setupRootProjectParams()
    template.executeRecipe()
  }
}