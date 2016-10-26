package cards.nine.commons.contentresolver

import android.database.Cursor
import cards.nine.commons.contentresolver.Conversions._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait ConversionsSpecification
  extends Specification
    with Mockito {

  trait ConversionsScope
    extends Scope {

    val mockCursor = mock[Cursor]
  }

}

class ConversionsSpec
  extends ConversionsSpecification {

  "getListFromCursor" should {

    "return an empty sequence when an empty cursor is given" in
      new ConversionsScope {

        mockCursor.moveToFirst() returns true
        val result = getListFromCursor[AnyRef](Cursor => AnyRef)(mockCursor)
        result should beEmpty
      }

  }
}
