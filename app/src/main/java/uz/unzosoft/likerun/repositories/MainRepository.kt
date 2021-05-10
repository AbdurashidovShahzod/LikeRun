package uz.unzosoft.likerun.repositories

import androidx.hilt.lifecycle.ViewModelInject
import uz.unzosoft.likerun.db.dao.RunDAO
import uz.unzosoft.likerun.db.entities.Run
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val runDAO: RunDAO
) {

    suspend fun insert(run: Run) = runDAO.insertRun(run)
    suspend fun delete(run: Run) = runDAO.deleteRun(run)

    fun getAllRunsSortedByDate() = runDAO.getAllRunsSortedByDate()
    fun getAllRunsSortedByDistance() = runDAO.getAllRunsSortedByDistance()
    fun getAllRunsSortedByAvgSpeed() = runDAO.getAllRunsSortedByAvgSpeed()
    fun getAllRunsSortedByCaloriesBurned() = runDAO.getAllRunsSortedByCaloriesBurned()
    fun getAllRunsSortedByTimeInMillis() = runDAO.getAllRunsSortedByTimeInMillis()

    fun getTotalCaloriesBurned() = runDAO.getTotalCaloriesBurned()
    fun getTotalAvgSpeed() = runDAO.getTotalAvgSpeed()
    fun getTotalDistance() = runDAO.getTotalDistance()
    fun getTotalTimeInMillis() = runDAO.getTotalTimeInMillis()


}