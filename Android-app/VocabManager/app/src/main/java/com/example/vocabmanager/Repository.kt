package com.example.vocabmanager

import android.content.Context
import com.example.vocabmanager.api.service.*
import com.example.vocabmanager.entities.*
import io.reactivex.rxjava3.core.Observable

class Repository(context: Context) {
    private var courseService: CourseService
    private var lessonService: LessonService
    private var statusService: StatusService
    private var poSService: PoSService
    private var vocabService: VocabService

    init {
        courseService = CourseService(context)
        lessonService = LessonService(context)
        statusService = StatusService(context)
        poSService = PoSService(context)
        vocabService = VocabService(context)
    }

    fun getAllCourses(searchStr: String): Observable<List<Course>> =
        courseService.getAllCourses(searchStr)

    fun addCourse(course: Course): Observable<Response> = courseService.saveCourse(course)

    fun updateCourse(course: Course): Observable<Response> = courseService.updateCourse(course)

    fun deleteCourse(id: Int) = courseService.deleteCourse(id)

    fun getAllLessons(
        searchStr: String?,
        courseID: String?,
        statusID: String?
    ) = lessonService.getAllLessons(searchStr, courseID, statusID)

    fun getAllStatus(): Observable<List<Status>> = statusService.getAllStatus()

    fun addLesson(lesson: Lesson): Observable<Response> = lessonService.saveLesson(lesson)

    fun deleteLesson(id: Int) = lessonService.deleteLesson(id)

    fun updateLesson(lesson: Lesson) = lessonService.updateLesson(lesson)

    fun getAllPoS(searchStr: String?) = poSService.getAllPoS(searchStr)

    fun addPoS(poS: PoS) = poSService.savePoS(poS)

    fun updatePoS(poS: PoS) = poSService.updatePoS(poS)

    fun deletePoS(id: Int) = poSService.deletePoS(id)

    fun getAllVocab(
        searchStr: String?,
        typeID: String?,
        lessonID: String?,
        page: Int
    ) = vocabService.getAllVocab(searchStr, typeID, lessonID, page)

    fun addVocab(vocab: Vocab) = vocabService.saveVocab(vocab)

    fun updateVocab(vocab: Vocab) = vocabService.updateVocab(vocab)

    fun deleteVocab(id: Int) = vocabService.deleteVocab(id)
}