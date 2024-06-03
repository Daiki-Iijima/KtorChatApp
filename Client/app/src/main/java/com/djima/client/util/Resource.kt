package com.djima.client.util

//  成功と失敗を表現
//  成功した場合と失敗した場合に同じクラスで戻り値を表現できるうえに、データの型を指定できるのでクラスに依存しない
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}