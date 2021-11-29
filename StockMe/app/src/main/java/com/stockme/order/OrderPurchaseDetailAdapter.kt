package com.stockme.order

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.stockme.R
import com.stockme.model.OrderPurchase
import com.stockme.model.ProductOrderPurchase

class OrderPurchaseDetailAdapter (private val orderPurchaseDetail: OrderPurchase):
    RecyclerView.Adapter<OrderPurchaseDetailAdapter.ViewHolder>(), Filterable {

    private var orderPurchaseProductListFiltered: ArrayList<ProductOrderPurchase> = orderPurchaseDetail.products

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_order_purchase_detail
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderPurchaseDetailAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderPurchaseDetailAdapter.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_order_purchase_detail -> {
                holder.itemView.findViewById<TextView>(R.id.descriptionText).text = orderPurchaseProductListFiltered[position].product.description
                holder.itemView.findViewById<TextView>(R.id.codeText).text = ("#" + orderPurchaseProductListFiltered[position].product.code)
                holder.itemView.findViewById<TextView>(R.id.stockOrdered).text    = (orderPurchaseProductListFiltered[position].stockOrdered.toString()    + " UN")
                if (orderPurchaseProductListFiltered[position].product.image != null) {
                    Picasso.get().load(Uri.parse(orderPurchaseProductListFiltered[position].product.image)).into(holder.itemView.findViewById<ImageView>(R.id.image))
                }
            }
            else -> {}
        }

    }
    override fun getItemCount(): Int = orderPurchaseProductListFiltered.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint?.toString() ?: ""
                var orderPurchaseProducts = orderPurchaseDetail.products
                if (charSearch.isNotEmpty()) orderPurchaseProducts =
                    orderPurchaseProducts.filter { orderPurchaseProduct ->
                        orderPurchaseProduct.product.code.lowercase().contains(charSearch.lowercase()) || orderPurchaseProduct.product.description.lowercase().contains(charSearch.lowercase())
                    } as ArrayList<ProductOrderPurchase>
                orderPurchaseProductListFiltered.clear()
                orderPurchaseProductListFiltered.addAll(orderPurchaseProducts)
                return FilterResults().apply { values = orderPurchaseProductListFiltered }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                orderPurchaseProductListFiltered = results?.values as ArrayList<ProductOrderPurchase>
                notifyDataSetChanged()
            }
        }
    }
}
