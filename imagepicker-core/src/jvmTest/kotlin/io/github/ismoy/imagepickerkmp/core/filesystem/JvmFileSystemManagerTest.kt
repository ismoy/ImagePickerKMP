package io.github.ismoy.imagepickerkmp.core.filesystem

import io.github.ismoy.imagepickerkmp.core.CoreServices
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import java.io.File

class JvmFileSystemManagerTest {

    private fun makeManager(subDir: String = "test_core_${System.nanoTime()}") =
        CoreServices.fileSystemManager(subDir)

    @Test
    fun createTempFile_returnsNonNullPath() = runTest {
        val manager = makeManager()
        val file = manager.createTempFile("prefix_", ".tmp")
        assertNotNull(file.path)
        assertTrue(file.path.isNotEmpty())
    }

    @Test
    fun createTempFile_prefixAppearedInFileName() = runTest {
        val manager = makeManager()
        val file = manager.createTempFile("myprefix", ".tmp")
        val name = File(file.path).name
        assertTrue(name.startsWith("myprefix"), "Expected name starting with 'myprefix', got '$name'")
    }

    @Test
    fun createTempFile_suffixAppearedInFileName() = runTest {
        val manager = makeManager()
        val file = manager.createTempFile("pre", ".wav")
        assertTrue(file.path.endsWith(".wav"), "Expected .wav suffix, got ${file.path}")
    }

    @Test
    fun createTempFile_emptySuffix_usesDefaultTmp() = runTest {
        val manager = makeManager()
        val file = manager.createTempFile("pre", "")
        assertTrue(file.path.endsWith(".tmp"), "Empty suffix should default to .tmp, got ${file.path}")
    }

    @Test
    fun createTempFile_fileExistsOnDisk() = runTest {
        val manager = makeManager()
        val file = manager.createTempFile("test_", ".tmp")
        assertTrue(File(file.path).exists(), "Created temp file must exist on disk")
    }

    @Test
    fun createTempFile_multipleCalls_differentPaths() = runTest {
        val manager = makeManager()
        val a = manager.createTempFile("aaa_", ".tmp")
        val b = manager.createTempFile("bbb_", ".tmp")
        assertTrue(a.path != b.path, "Each temp file must have a unique path")
    }

    @Test
    fun getCacheDirectory_returnsNonNullPath() = runTest {
        val manager = makeManager()
        val dir = manager.getCacheDirectory()
        assertNotNull(dir.path)
        assertTrue(dir.path.isNotEmpty())
    }

    @Test
    fun getCacheDirectory_dirExistsAfterCall() = runTest {
        val subDir = "kover_test_cache_${System.nanoTime()}"
        val manager = makeManager(subDir)
        val dir = manager.getCacheDirectory()
        assertTrue(File(dir.path).isDirectory, "Cache directory must be a directory: ${dir.path}")
    }

    @Test
    fun getCacheDirectory_containsSubdirectoryName() = runTest {
        val subDir = "unique_sub_${System.nanoTime()}"
        val manager = makeManager(subDir)
        val dir = manager.getCacheDirectory()
        assertTrue(dir.path.contains(subDir), "Cache dir path must contain subDir name '$subDir', got ${dir.path}")
    }

    @Test
    fun clearCache_doesNotThrow() = runTest {
        val manager = makeManager()
        manager.createTempFile("toDelete_", ".tmp")
        manager.clearCache() // no exception
    }

    @Test
    fun clearCache_removesCreatedFiles() = runTest {
        val subDir = "clear_test_${System.nanoTime()}"
        val manager = makeManager(subDir)
        val file = manager.createTempFile("willDelete_", ".tmp")
        assertTrue(File(file.path).exists(), "File should exist before clearCache")
        manager.clearCache()
        val dirAfter = File(manager.getCacheDirectory().path)
        val remaining = dirAfter.listFiles() ?: emptyArray()
        assertEquals(0, remaining.size, "After clearCache, directory must be empty")
    }

    @Test
    fun clearCache_canBeCalledOnEmptyDir() = runTest {
        val manager = makeManager("empty_dir_${System.nanoTime()}")
        manager.clearCache() // no files yet — should not throw
    }

    @Test
    fun getCacheDirectory_calledTwice_samePath() = runTest {
        val manager = makeManager("stable_dir_${System.nanoTime()}")
        val dir1 = manager.getCacheDirectory()
        val dir2 = manager.getCacheDirectory()
        assertEquals(dir1.path, dir2.path)
    }
}
