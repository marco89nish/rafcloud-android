package rs.mmitic.rafcloud.data.model

import org.threeten.bp.LocalDate

typealias Machine = MachineDTO

data class MachineDTO(
    val name: String,
    val uid: String,
    val status: MachineStatus,
    val createdBy: String,
    val creationDate: LocalDate
)

enum class MachineStatus(val transitionStatus: MachineStatus? = null) {
    STOPPING, STOPPED(STOPPING), STARTING, RUNNING(STARTING)
}

val MachineStatus.isTemporary: Boolean
    get() = this.transitionStatus == null

data class SearchModel(
    var name: String? = null,
    var status: List<String>? = null,
    //@field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var dateFrom: LocalDate? = null,
    //@field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var dateTo: LocalDate? = null
)