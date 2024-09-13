package com.example.playlist_maker.data.dto

//Это родительский класс ответов от сервера
open class Response () {
    //Меняя значение этой  переменной мы будем сообщать
    // слою Domain, успешно ли завершилось выполнение запроса.
    var resultCode = 0
    var message = ""
}