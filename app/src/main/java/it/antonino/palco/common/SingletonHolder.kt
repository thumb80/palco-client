package it.antonino.palco.common

open class SingletonHolderTwoInput<out T, in A,B>(creator: (A,B) -> T) {

    private var creator: ((A,B) -> T)? = creator
    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A, arg2: B): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg,arg2)
                instance = created
                creator = null
                created
            }
        }
    }
}

open class SinglettonHolderThreeInput<out T, in A,B,C>(creator: (A,B,C) -> T) {

    private var creator: ((A,B,C) -> T)? = creator
    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A, arg2: B, arg3: C): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg,arg2,arg3)
                instance = created
                creator = null
                created
            }
        }
    }

}

open class SingletonHolder<out T, in A>(creator: (A) -> T) {

    private var creator: ((A) -> T)? = creator
    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}