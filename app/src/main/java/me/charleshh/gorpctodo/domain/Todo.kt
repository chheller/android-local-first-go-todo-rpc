package domain

import java.time.LocalDateTime

class Todo(val title: String, val done: Boolean, val createdAt: LocalDateTime, val modifiedAt: LocalDateTime) {
}