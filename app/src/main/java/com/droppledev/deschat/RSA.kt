package com.droppledev.deschat

import java.math.BigInteger


class RSA{
    lateinit var publicKey : List<Int>
    lateinit var privateKey : List<Int>

    fun init(p: Int, q : Int){
        val n = p * q
        val phi = (p-1)*(q-1)
        val e = generateE(phi)
        val d = modInverse(e,phi)
        publicKey = listOf(e,n)
        privateKey = listOf(d,n)
    }

    fun encrypt(m: BigInteger, pb: List<Int>):Int{
        val e = pb[0]
        val n = pb[1].toBigInteger()
        val res = m.pow(e).mod(n)
        return res.toInt()
    }
    fun decrypt(c: BigInteger, pv: List<Int>):Int{
        val d = pv[0]
        val n = pv[1].toBigInteger()
        val res = c.pow(d).mod(n)
        return res.toInt()
    }


    fun generateE(phi : Int):Int
    {
        var eTemp = 2
        while (eTemp < phi) {
            if (gcd(eTemp, phi) == 1)
            {
                break
            }
            eTemp++
        }
        return eTemp
    }

    fun gcd(a:Int, b:Int):Int {
        if (b == 0)
            return a
        return gcd(b, a % b)
    }

    fun modInverse(a: Int, m: Int): Int {
        var a = a
        a %= m
        for (x in 1 until m)
            if (a * x % m == 1)
                return x
        return 1
    }


}