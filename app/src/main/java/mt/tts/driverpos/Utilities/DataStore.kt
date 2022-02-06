package mt.tts.driverpos.Utilities

import mt.tts.driverpos.Model.Customer

class DataStore {
    var customers: ArrayList<Customer> = ArrayList<Customer>()

    companion object {
        val instance = DataStore()

        fun GetCustomer(ccode: String): Customer
        {
            for (c in DataStore.instance.customers) {
                if (c.ClientCode == ccode) {
                    return c
                }
            }

            return DataStore.instance.customers[0] //not found!!
        }
    }
}