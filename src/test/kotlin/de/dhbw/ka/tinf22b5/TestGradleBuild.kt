package de.dhbw.ka.tinf22b5

import de.dhbw.ka.tinf22b5.util.ProjectVersionUtil
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class TestGradleBuild {
    @Test
    fun testGradleBuild() {
        assertNotNull(ProjectVersionUtil.getProjectVersion())
        assertNotEquals(ProjectVersionUtil.getProjectVersion(), "unknown")
        assertNotEquals(ProjectVersionUtil.getProjectVersion(), "not set")
    }
}