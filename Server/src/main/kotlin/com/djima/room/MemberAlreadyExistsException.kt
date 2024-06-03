package com.djima.room

//  すでにメンバーが存在しているときに発生する例外
class MemberAlreadyExistsException: Exception(
    "このルームではすでにメンバーになっています"
)