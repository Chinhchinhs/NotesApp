package com.example.notesapp.util

import android.content.Context
import android.content.Intent
import com.example.notesapp.model.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportUtil {
    fun shareNoteAsText(context: Context, note: Note) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val text = buildString {
            append("Tiêu đề: ${note.title}\n\n")
            append("Nội dung:\n${note.content}\n\n")
            append("Phân loại: ${if (note.category.isBlank()) "Chưa phân loại" else note.category}\n")
            append("Ngày tạo: ${sdf.format(Date(note.createdAt))}\n")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Xuất ghi chú"))
    }
}
