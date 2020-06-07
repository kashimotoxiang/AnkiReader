package com.blanke.ankireader.bean

data class Deck(var id: Long, var name: String) {
    override fun toString(): String {
        return "Deck{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}'
    }

}
