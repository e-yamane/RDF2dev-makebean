package jp.rough_diamond.dev.bean

import org.scalatest.Spec
import java.io.File
import java.util.Date
import org.apache.commons.io.FileUtils._
import org.apache.commons.lang3.time.DateUtils._

class MakeBeanPluginSpec extends Spec {
	val testDir = new File("testDir")
	deleteQuietly(testDir)
	var cache = MakeBeanPlugin.getCacheFile(testDir)
	var destDir = new File(testDir, "src")
	
	describe("makeBeanを初めて実行しようとした時") {
		it("JavaBeans定義ファイルが存在しない場合は実行せず、実行記録ファイルも作成されていない事") {
			cache.delete()
			var notFound = new File("notFound.xml")
			notFound.delete()
			assert(!MakeBeanPlugin.isDo(notFound, testDir))
			assert(!cache.exists)
		}
		it("javaBeans定義ファイルがあれば実行される") {
			cache.delete()
			var beanDefXml = copyBeanDef
			assert(MakeBeanPlugin.isDo(beanDefXml, testDir))
		}
		it("makeBean実行時に実行記録ファイルが作成されている事") {
			cache.delete()
			var beanDefXml = copyBeanDef
			MakeBeanPlugin.doMakeBean(beanDefXml, testDir, destDir)
			assert(cache.exists)
		}
	}
	
	describe("makeBeanを二度目以降に実行しようとした時に") {
		it("以前実行したときからJavaBeans定義ファイルが更新されてない場合は実行せず実行記録ファイルも更新されない事") {
			touch(cache)
			cache.setLastModified(addHours(new Date(), -1).getTime)
			var beanDefXml = copyBeanDef
			beanDefXml.setLastModified(addHours(new Date(), -2).getTime)
			assert(!MakeBeanPlugin.isDo(beanDefXml, testDir))
			//最終更新日時を１時間前にセットしているので時間経緯上１時間以上になっていればよい
			assert((System.currentTimeMillis - cache.lastModified) >= 1 * 60 * 60 * 1000)
		}
		it("以前実行したときからJavaBeans定義ファイルが更新されている場合は実行する") {
			touch(cache)
			cache.setLastModified(addHours(new Date(), -2).getTime)
			var beanDefXml = copyBeanDef
			beanDefXml.setLastModified(addHours(new Date(), -1).getTime)
			assert(MakeBeanPlugin.isDo(beanDefXml, testDir))
		}
		it("makeBean実行時に実行記録ファイルが更新されていること") {
			touch(cache)
			cache.setLastModified(addHours(new Date(), -2).getTime)
			var beanDefXml = copyBeanDef
			beanDefXml.setLastModified(addHours(new Date(), -1).getTime)
			MakeBeanPlugin.doMakeBean(beanDefXml, testDir, destDir)
			assert(cache.exists)
			//最終更新日時を２時間前にセットしているので時間経緯上２時間未満になっていればよい
			assert((System.currentTimeMillis - cache.lastModified) < 2 * 60 * 60 * 1000)
		}
	}
	
	def copyBeanDef :File = {
		var in = new File("src/test/beanDef.xml")
		var out = new File(testDir, "beanDef.xml")
		copyFile(in, out)
		out
	}
}