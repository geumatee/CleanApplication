package io.heartworks.cleanapplication.dog.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.heartworks.cleanapplication.R

/**
 * This activity holds the DogsListFragment and does nothing special
 */
class DogsListActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_dogs_list)

    if (savedInstanceState == null) {
      // Create the fragment and display it
      val dogsListFragment = DogsListFragment()
      supportFragmentManager.beginTransaction().replace(R.id.fragment_container, dogsListFragment).commit()
    }
  }

}