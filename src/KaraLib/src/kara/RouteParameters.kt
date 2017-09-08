package kara

import kotlinx.reflection.MissingArgumentException
import kotlinx.reflection.RECORD_SEPARATOR_CHAR
import java.util.*

/** Contains all of the parameters for a matched route. */
class RouteParameters {
    internal val _map = HashMap<String, String>()
    internal val _list = ArrayList<String>()

    fun parameterNames() = _map.keys.toList()
    /** Gets a named parameter by name */
    operator fun get(name : String) : String? = _map[name]

    /** Sets a named parameter */
    operator fun set(name : String, value : String) {
        if (!_map.containsKey(name)) { // add it to the unnamed list as well, if it's not already there
            append(value)
            _map[name] = value
        } else {
            _map[name] = "${_map[name]}$RECORD_SEPARATOR_CHAR$value"
        }
    }

    /** Gets an unnamed parameter by index */
    operator fun get(i : Int) : String? = _list[i]

    /** Sets an unnamed parameter */
    operator fun set(i : Int, value : String) {
        _list[i] = value
    }

    /** Apends an unnamed paramter */
    fun append(value : String) {
        _list.add(value)
    }

    /** Returns the total number of parameters */
    fun size() : Int = _list.size

    /** Gets a hash with the nested values of the given name. */
    fun getHash(name : String) : HashMap<String,String> {
        val map = HashMap<String,String>()
        val prefix = name + "["
        for (key in _map.keys) {
            if (key.startsWith(prefix)) {
                var subkey = key.replace(prefix, "")
                subkey = subkey.substring(0, subkey.length -1)
                val value = _map[key]!!
                map[subkey] = value
            }
        }
        return map
    }

    override fun toString() : String =
        _map.toList().joinToString(", ") { (key, value) ->
            "$key: $value"
        }

    fun optIntParam(name: String): Int? = optStringParam(name)?.toIntOrNull()


    fun intParam(name: String): Int =
            optIntParam(name) ?: throw MissingArgumentException("Required int argument $name is missing")

    fun stringParam(name: String): String =
            optStringParam(name) ?: throw MissingArgumentException("Required string argument $name is missing")

    fun optStringParam(name: String): String? = this[name]

    fun optListParam(name: String): List<String>? = this[name]?.split(RECORD_SEPARATOR_CHAR)
}
