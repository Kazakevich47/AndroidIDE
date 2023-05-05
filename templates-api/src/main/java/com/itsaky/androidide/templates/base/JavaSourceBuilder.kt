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

package com.itsaky.androidide.templates.base

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec

/**
 * Utility for building Java source files.
 *
 * @author Akash Yadav
 */
class JavaSourceBuilder {

  /**
   * The type of Java source. A [SourceType] creates a [TypeSpec.Builder] and optionally
   * pre-configures it.
   */
  interface SourceType {

    /**
     * Create the [TypeSpec.Builder] for building this type of Java source file.
     */
    fun builder(klass: ClassName): TypeSpec.Builder
  }

  /**
   * A class type.
   */
  class ClassType : SourceType {

    override fun builder(klass: ClassName): TypeSpec.Builder {
      return TypeSpec.classBuilder(klass)
    }
  }

  /**
   * An interface type.
   */
  class InterfaceType : SourceType {

    override fun builder(klass: ClassName): TypeSpec.Builder {
      return TypeSpec.interfaceBuilder(klass)
    }
  }

  /**
   * An enum type.
   */
  class EnumType : SourceType {

    override fun builder(klass: ClassName): TypeSpec.Builder {
      return TypeSpec.enumBuilder(klass)
    }
  }

  /**
   * An annotation interface type.
   */
  class AnnotationType : SourceType {

    override fun builder(klass: ClassName): TypeSpec.Builder {
      return TypeSpec.annotationBuilder(klass)
    }
  }

  private val files = hashSetOf<JavaFile>()

  /**
   * Creates a new Java class.
   *
   * @param packageName The package name of the class.
   * @param className The name of the class.
   * @param configure Function to configure the [TypeSpec.Builder].
   */
  fun newClass(packageName: String, className: String, configure: TypeSpec.Builder.() -> Unit) {
    return newJavaFile(packageName, className, ClassType(), configure)
  }

  /**
   * Creates a new Java enum class.
   *
   * @param packageName The package name of the class.
   * @param className The name of the class.
   * @param configure Function to configure the [TypeSpec.Builder].
   */
  fun newEnum(packageName: String, className: String, configure: TypeSpec.Builder.() -> Unit) {
    return newJavaFile(packageName, className, EnumType(), configure)
  }

  /**
   * Creates a new Java interface.
   *
   * @param packageName The package name of the interface.
   * @param className The name of the interface.
   * @param configure Function to configure the [TypeSpec.Builder].
   */
  fun newInterface(packageName: String, className: String, configure: TypeSpec.Builder.() -> Unit) {
    return newJavaFile(packageName, className, InterfaceType(), configure)
  }

  /**
   * Creates a new Java annotation interface.
   *
   * @param packageName The package name of the interface.
   * @param className The name of the interface.
   * @param configure Function to configure the [TypeSpec.Builder].
   */
  fun newAnnotation(packageName: String, className: String, configure: TypeSpec.Builder.() -> Unit
  ) {
    return newJavaFile(packageName, className, AnnotationType(), configure)
  }

  /**
   * Creates a new Java class.
   *
   * @param packageName The package name of the class.
   * @param className The name of the class.
   * @param type The type of the class.
   * @param configure Function to configure the [TypeSpec.Builder].
   */
  fun newJavaFile(packageName: String, className: String, type: SourceType,
                  configure: TypeSpec.Builder.() -> Unit
  ) {
    val klass = ClassName.get(packageName, className)
    val builder = type.builder(klass).apply(configure)
    val file = JavaFile.builder(packageName, builder.build())
    file.skipJavaLangImports(true)
    files.add(file.build())
  }

  internal fun ModuleTemplateBuilder.write() {
    for (file in files) {
      file.writeTo(mainJavaSrc())
    }
  }

  private fun JavaFile.packNameToPath(): String {
    return packageName.replace('.', '/')
  }
}
