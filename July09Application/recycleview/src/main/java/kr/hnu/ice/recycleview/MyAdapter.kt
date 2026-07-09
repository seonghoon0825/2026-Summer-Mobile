package kr.hnu.ice.recycleview

import android.widget.Toast
import kr.hnu.ice.recycleview.databinding.ItemMainBinding

class MyAdapter(val datas: List<String>) : androidx.recyclerview.widget.RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private val mDatas = datas
    class MyViewHolder(val binding: ItemMainBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemMainBinding.inflate(android.view.LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.itemData.text = mDatas[position]
        holder.binding.itemRoot.setOnClickListener {
            Toast.makeText(holder.itemView.context,
                "Clicked: ${mDatas[position]}",
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return mDatas.size
    }
}