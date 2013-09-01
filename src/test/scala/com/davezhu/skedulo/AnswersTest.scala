package com.davezhu.skedulo

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.davezhu.skedulo.Answers.{Product, OrderProduct, SQL}

/**
 * @author: dzhu
 */
class AnswersTest extends FlatSpec with ShouldMatchers {

  val EXPECTED_FETCH_PRODUCTS_SQL_1 =
    """
      | SELECT p.product_id as "product_id", name, price
      |   FROM OrderProduct op, Product p
      |   WHERE op.product_id = p.product_id AND order_id IN (?,?) AND product_id IN (?,?)
      |   ORDER BY op.order_id, p.product_id ASC
    """.stripMargin

  val EXPECTED_FETCH_PRODUCTS_SQL_2 =
    """
      | SELECT p.product_id as "product_id", name, price
      |   FROM OrderProduct op, Product p
      |   WHERE op.product_id = p.product_id AND order_id IN (?) AND product_id IN (?)
      |   ORDER BY op.order_id, p.product_id ASC
    """.stripMargin

  it should "(a1) return correct string for number" in {
    Answers.toString(1) should be("1")
    Answers.toString(4) should be("Hello")
    Answers.toString(5) should be("World")
    Answers.toString(20) should be("HelloWorld")
    Answers.toString(21) should be("21")
  }

  it should "(a2) return correctly populated OrderProducts" in {

    val p5 = Product(5, "product 5", 10.00)
    val p9 = Product(9, "product 9", 12.00)
    val p10 = Product(10, "product 10", 11.00)

    implicit def assertingFetcher(sql: SQL, params: Array[_]): Array[Product] = {
      // yuck if/else in tests...
      if (sql == EXPECTED_FETCH_PRODUCTS_SQL_1) {
        require(params zip Array(1, 1, 5, 10) forall {
          case (l, r) => l == r
        })
        Array(p5, p10)
      } else if (sql == EXPECTED_FETCH_PRODUCTS_SQL_2) {
        require(params zip Array(3, 9) forall {
          case (l, r) => l == r
        })
        Array(p9)
      } else {
        fail("")
      }
    }

    Answers.a2(ids = Array((3, 9), (1, 10), (1, 5)), chunkSize = 2) should be(
      Seq(OrderProduct(1, p5), OrderProduct(1, p10), OrderProduct(3, p9))
    )

  }

  it should "(a2) not fetch more than 10,000" in {
    try {
      Answers.a2((0 to 10000).map(i => (i, i))(collection.breakOut))({
        case (sql, params) => fail("Should not fetch more than 10,000 records")
      })
    } catch {
      case e: IllegalArgumentException => () // ok
    }
  }

}
