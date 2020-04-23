package com.homurax.retrofittest

import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun main() {
    // 创建一个协程的作用域
    // 创建的是顶层协程 协程当应用程序运行结束时也会跟着一起结束
    /*GlobalScope.launch {
        println("codes run in coroutine scope")
        // 非阻塞式的挂起函数 只挂起当前协程 并不会影响其他协程的运行
        delay(1500)
        println("codes run in coroutine scope finished")
    }
    Thread.sleep(1000)*/

    // 保证在协程作用域内的所有代码和子协程没有全部执行完之前一直阻塞当前线程
    /*runBlocking {
        println("codes run in coroutine scope")
        delay(1500)
        println("codes run in coroutine scope finished")
    }
    Thread.sleep(1000)*/

    // 创建多个协程
    /*runBlocking {
        launch {
            println("launch1")
            delay(1000)
            println("launch1 finished")
        }
        launch {
            println("launch2")
            delay(1000)
            println("launch2 finished")
        }
    }*/

    // 开启 100000 个协程
    /*val start = System.currentTimeMillis()
    runBlocking {
        repeat(100000) {
            launch {
                println(".")
            }
        }
    }
    val end = System.currentTimeMillis()
    println(end - start)*/

    /*runBlocking {
        coroutineScope {
            launch {
                for (i in 1..10) {
                    println(i)
                    delay(1000)
                }
                println("coroutineScope finished")
            }
        }
        println("runBlocking finished")
    }*/

    /*val job = Job()
    val scope = CoroutineScope(job)
    scope.launch {
        // TODO
    }
    job.cancel()*/

    /*runBlocking {
        val result = async {
            5 + 5
        }.await()
        println(result)
    }*/

    /*runBlocking {
        // 两个 async 是串行的关系
        val start = System.currentTimeMillis()
        val result1 = async {
            delay(1000)
            5 + 5
        }.await()
        val result2 = async {
            delay(1000)
            4 + 6
        }.await()
        println("result is ${result1 + result2}")
        val end = System.currentTimeMillis()
        println("cost ${end - start} ms.")
    }*/

    // 两个 async 是并行关系
    /*runBlocking {
        val start = System.currentTimeMillis()
        val deferred1 = async {
            delay(1000)
            5 + 5
        }
        val deferred2 = async {
            delay(1000)
            4 + 6
        }
        println("result is ${deferred1.await() + deferred2.await()}")
        val end = System.currentTimeMillis()
        println("cost ${end - start} ms.")
    }*/

    runBlocking {
        val result = withContext(Dispatchers.Default) {
            5 + 5
        }
        println(result)
    }
}

suspend fun printDot() = coroutineScope {
    launch {
        println(".")
        delay(1000)
    }
}

suspend fun getAppData() {
    try {
        val appList = ServiceCreator.create<AppService>().getAppData().await()
        println(appList)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun <T> Call<T>.await(): T {
    return suspendCoroutine { continuation ->
        enqueue(object : Callback<T> {

            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                if (body != null) {
                    continuation.resume(body)
                } else {
                    continuation.resumeWithException(RuntimeException("response body is null"))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}










