package com.davezhu.skedulo

import scala.collection.immutable.IndexedSeq

/**
 * @author: dzhu
 */
object Answers {

  type OrderId = Int
  type ProductId = Int

  type SQL = String

  val FETCH_PRODUCTS =
    """
      | SELECT p.product_id as "product_id", name, price
      |   FROM OrderProduct op, Product p
      |   WHERE op.product_id = p.product_id AND order_id IN (%s) AND product_id IN (%s)
      |   ORDER BY op.order_id, p.product_id ASC
    """.stripMargin

  def a1 {
    for (i <- 1 to 100) {
      print(toString(i))
    }
  }

  def a2(ids: Array[(OrderId, ProductId)], chunkSize: Int = 500)(implicit fetch: (SQL, Array[_]) => Array[Product]): Seq[OrderProduct] = {

    require(ids.size <= 10000)

    // NOTE: the default chunkSize of 500 ensures no more than 20 SQL queries are executed...

    val sorted = ids.sortBy {
      // sort first by orderId, then by productId (ASC order)
      case ((orderId, productId)) => orderId -> productId
    }

    for {
      i <- 0 until sorted.size by chunkSize
      j = math.min(sorted.length, i + chunkSize)
      chunk = sorted.slice(i, j)
      q = "?," * (chunk.length - 1) + "?"
      params = chunk.map(_._1) ++ chunk.map(_._2)
      orderProduct <- (chunk zip fetch(FETCH_PRODUCTS.format(q, q), params) map {
        case ((orderId, _), p) => OrderProduct(orderId, p)
      })
    } yield orderProduct

  }

  def a3 {

    // SQL statement that returns the name of all products ordered by ClientId '3'
    """
      | SELECT DISTINCT(description) FROM Order o, OrderProduct op
      |   WHERE o.order_id = op.order_id AND o.client_id = 3
      |
    """.stripMargin

    // SQL statement that summarises the total price of all orders, on a per client basis
    """
      | SELECT SUM(op.price), c.name FROM Order o, Client c, OrderProduct op
      |   WHERE o.order_id = op.order_id AND o.client_id = c.client_id
      |   GROUP BY (c.name)
    """.stripMargin

  }

  def toString(i: Int) = {
    val divBy4 = i % 4 == 0
    val divBy5 = i % 5 == 0
    if (divBy4 && divBy5) "HelloWorld"
    else if (divBy4) "Hello"
    else if (divBy5) "World"
    else i.toString
  }

  case class OrderProduct(orderId: OrderId, productId: ProductId, description: String, price: Double)

  object OrderProduct {
    def apply(orderId: OrderId, p: Product) = new OrderProduct(orderId, p.productId, p.name, p.price)
  }

  case class Product(productId: ProductId, name: String, price: Double)

}
