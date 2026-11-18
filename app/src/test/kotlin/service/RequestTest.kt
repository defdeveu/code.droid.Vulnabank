package defdev.labs.droid.vulnabank.service

import org.junit.Assert.assertEquals
import org.junit.Test

class RequestTest {

    @Test
    fun xmlContainsAllThreeElementsInOrder() {
        assertEquals(
            "<request><enckey>a</enckey><message>b</message><signature>c</signature></request>",
            Request("a", "b", "c").toXml(),
        )
    }
}
