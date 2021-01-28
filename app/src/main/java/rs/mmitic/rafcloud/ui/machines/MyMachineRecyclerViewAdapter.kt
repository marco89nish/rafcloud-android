package rs.mmitic.rafcloud.ui.machines


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_machine.view.*
import rs.mmitic.rafcloud.R
import rs.mmitic.rafcloud.data.model.Machine
import rs.mmitic.rafcloud.data.model.MachineStatus.*
import rs.mmitic.rafcloud.data.model.isTemporary
import rs.mmitic.rafcloud.ui.machines.MachineFragment.MachineInteractionListener
import rs.mmitic.rafcloud.ui.machines.MachineFragment.MachineInteractionListener.*
import rs.mmitic.rafcloud.ui.machines.MachineFragment.MachineInteractionListener.Action.*

/**
 * [RecyclerView.Adapter] that can display a Machine and makes a call to the
 * specified [MachineInteractionListener].
 */
class MyMachineRecyclerViewAdapter(
    private var values: List<Machine>,
    private val listener: MachineInteractionListener
) : RecyclerView.Adapter<MyMachineRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_machine, parent, false)
        return ViewHolder(view)
    }

    fun updateData(data: List<Machine>) {
        values = data
        this.notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val machine = values[position]

        holder.name.text = machine.name
        holder.status.text = machine.status.toString()

        holder.startStopBtn.apply {
            setImageResource(when (machine.status) {
                RUNNING -> R.drawable.ic_stop_24px
                STOPPED -> R.drawable.ic_play_arrow_black_24dp
                else -> R.drawable.ic_hourglass_bottom_24px
            })
            isEnabled = !machine.status.isTemporary
            imageAlpha = if (isEnabled) 0xFF else 0x3F // grays out disabled btn
            setOnClickListener {
                val action = if (machine.status == RUNNING) STOP else START
                listener.onAction(machine, action)
            }
        }

        holder.deleteBtn.apply {
            isEnabled = machine.status == STOPPED
            imageAlpha = if (isEnabled) 0xFF else 0x3F // grays out disabled btn
            setOnClickListener {
                listener.onAction(machine, DELETE)
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val name: TextView = mView.name
        val status: TextView = mView.status
        val startStopBtn: ImageButton = mView.startStopBtn
        val deleteBtn: ImageButton = mView.deleteBtn
    }
}

