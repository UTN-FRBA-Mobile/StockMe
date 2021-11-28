package com.stockme.order
import com.stockme.model.OrderPurchase

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.type.Date
import com.squareup.picasso.Picasso
import com.stockme.R

class OrderPurchaseAdapter (private val orderPurchases: List<OrderPurchase>):
    RecyclerView.Adapter<OrderPurchaseAdapter.ViewHolder>(), Filterable {

    private var orderPurchaseListFiltered: ArrayList<OrderPurchase> = ArrayList(orderPurchases)

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_order_purchase
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderPurchaseAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderPurchaseAdapter.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_order_purchase -> {
                holder.itemView.findViewById<TextView>(R.id.code).text = orderPurchaseListFiltered[position].code
                holder.itemView.findViewById<TextView>(R.id.createdDate).text = (orderPurchaseListFiltered[position].createdDate)
                holder.itemView.findViewById<TextView>(R.id.supplier).text = orderPurchaseListFiltered[position].supplier
            }
            else -> {}
        }
    }
    override fun getItemCount(): Int = orderPurchaseListFiltered.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint?.toString() ?: ""
                var OrderPurchaseList = orderPurchases
                if (charSearch.isNotEmpty()) OrderPurchaseList = OrderPurchaseList.filter { OrderPurchase ->
                    OrderPurchase.code.lowercase().contains(charSearch.lowercase()) || OrderPurchase.supplier.lowercase().contains(charSearch.lowercase())
                }
                orderPurchaseListFiltered.clear()
                orderPurchaseListFiltered.addAll(OrderPurchaseList)
                return FilterResults().apply { values = orderPurchaseListFiltered }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                orderPurchaseListFiltered = results?.values as ArrayList<OrderPurchase>
                notifyDataSetChanged()
            }
        }
    }
}
