package it.antonino.palco.model

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CityListAdapterTest {

    val cityListAdapter = CityListAdapter(
        arrayListOf("Roma","Ciampino"), {

        }
    )

    @Test
    fun cityListAdapterTest() {
        assert(cityListAdapter.city?.isEmpty() == false)
        assert(cityListAdapter.cities == null)
        assert(cityListAdapter.itemCount == 2)
    }

}