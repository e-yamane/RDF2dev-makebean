package jp.rough_diamond.dev.bean

import sbt._
import Keys._
import java.io.{File, FileOutputStream}
import org.apache.commons.io.FileUtils._

import jp.rough_diamond.dev.core.RdfDevCore

object MakeBeanPlugin extends Plugin {
	val CHECH_NAME = "rdf/makeBean.chache"
	val beanDefXml = SettingKey[File]("bean-def-xml", "JavaBeans定義ファイル")
	val makeBean   = TaskKey[Unit]("make-bean", "JavaBeansを生成します")
	override val settings = inConfig(RdfDevCore.rdf)(Seq(
		beanDefXml <<= baseDirectory(_ / "etc" / "beanDef" / "beanDef.xml")
		,makeBean  <<= (beanDefXml, target, (javaSource in Compile), javacOptions) map { 
			(srcXml, targetDir, destDir, compileOption) => doMakeBean(srcXml, targetDir, destDir, getEncoding(compileOption)) 
		}
	)) ++ Seq(
		compile in Compile <<= (compile in Compile) dependsOn (makeBean in RdfDevCore.rdf)
	)
	
	def getEncoding(compileOptions:Seq[String]) : String = {
		val index = compileOptions.indexWhere(s => s== "-encoding")
		if(index == -1) {
			"UTF-8"
		} else {
			compileOptions.apply(index + 1)
		}
	}
	
	def doMakeBean(srcXml : File, targetDir:File, destDir:File, encoding:String) = {
		if(isDo(srcXml, targetDir)) {
			new jp.rough_diamond.tools.beangen.JavaBeansGenerator(srcXml, destDir, encoding).doIt
			touch(getCacheFile(targetDir))
		}
	}
	
	def isDo(srcXml : File, target : File) : Boolean = {
		if(!srcXml.exists) {
			return false
		}
		val cache = getCacheFile(target)
		if(!cache.exists) {
			return true
		}
		cache.lastModified < srcXml.lastModified
	}

	def getCacheFile(dir : File) : File = {
		return new File(dir, "rdf/makeBean.chache")
	}
}
