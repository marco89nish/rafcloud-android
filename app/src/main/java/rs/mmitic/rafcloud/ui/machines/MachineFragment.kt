package rs.mmitic.rafcloud.ui.machines

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import rs.mmitic.rafcloud.R
import rs.mmitic.rafcloud.data.LoginRepository
import rs.mmitic.rafcloud.data.model.Machine
import rs.mmitic.rafcloud.network.NetworkFactory
import rs.mmitic.rafcloud.ui.login.LoginActivity
import rs.mmitic.rafcloud.ui.machines.MachineFragment.MachineInteractionListener.Action
import rs.mmitic.rafcloud.ui.machines.MachineFragment.MachineInteractionListener.Action.*


/**
 * A fragment representing a list of Machines.
 */
class MachineFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val machineList = inflater.inflate(R.layout.fragment_machine_list, container, false) as RecyclerView

        // Set the adapter
        machineList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = machineListAdapter
        }

        return machineList
    }

    private var loaderJob: Job? = null

    /** Handles user actions originating from the machine list. */
    private val machineInteractionListener = { machine: Machine, action: Action ->
        lifecycleScope.launch {
            runCatching {
                val machineService = NetworkFactory.machineService
                val (networkCall, userMsg) = when (action) {
                    START -> machineService::start to "Starting machine"
                    STOP -> machineService::stop to "Stopping machine"
                    DELETE -> machineService::destroy to "Deleted machine"
                }

                if (networkCall(machine.uid).isSuccessful) {
                    loadMachinesFromNetwork()
                    showToast(userMsg)
                } else showToast("Server rejected request")
            }.onFailure { ex ->
                Log.w("MachineFragment", "Network call failed", ex)
                showToast("Network call failed")
            }
        }
        Unit
    }
    private val machineListAdapter = MyMachineRecyclerViewAdapter(emptyList(), machineInteractionListener)

    override fun onResume() {
        super.onResume()
        loaderJob = lifecycleScope.launch {
            while (true) {
                loadMachinesFromNetwork()
                delay(10_000)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        loaderJob?.cancel()
        loaderJob = null
    }

    private suspend fun loadMachinesFromNetwork() {
        runCatching {
            val machines = NetworkFactory.machineService.getAll()
            machineListAdapter.updateData(machines)
        }.onFailure {
            machineListAdapter.updateData(emptyList())
            showToast("Failed to fetch data")
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.machines_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.add -> {
            showNewMachineDialog()
            true
        }
        R.id.refresh -> {
            lifecycleScope.launch {
                loadMachinesFromNetwork()
                Toast.makeText(context, "Loaded!", Toast.LENGTH_SHORT).show()
            }
            true
        }
        R.id.logout -> {
            LoginRepository.logout()
            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
            true
        }
        else -> false
    }

    fun showNewMachineDialog() {
        val machineNameET = EditText(context!!).apply {
            hint = "(autogenerate)"
        }

        val layout = LinearLayout(context!!).apply {
            orientation = LinearLayout.VERTICAL
            addView(TextView(context!!).apply { text = "Enter machine name:" })
            addView(machineNameET)
            val paddingPx = 20 * context.resources.displayMetrics.density
            setPadding(paddingPx.toInt())
        }

        val dialogBuilder = AlertDialog.Builder(context!!).apply {
            setTitle("Add new machine")
            setView(layout)
            setPositiveButton("Create") { _, _ ->
                val machineName = machineNameET.text.toString().takeIf { it.isNotEmpty() }
                //todo progress dialog
                lifecycleScope.launch {
                    runCatching {
                        val response = NetworkFactory.machineService.create(machineName)
                        if (response.isSuccessful) loadMachinesFromNetwork()
                        else showToast("Failed to create new machine")
                    }.onFailure {
                        showToast("Network call failed")
                    }
                }
            }
            setCancelable(true)
            setNegativeButton("Cancel") { _, _ -> }
        }

        dialogBuilder.create().show()
    }

    fun interface MachineInteractionListener {
        enum class Action {
            START, STOP, DELETE
        }

        fun onAction(item: Machine, action: Action)
    }
}
