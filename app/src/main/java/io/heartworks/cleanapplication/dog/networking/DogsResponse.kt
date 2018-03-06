package io.heartworks.cleanapplication.dog.networking

import io.heartworks.cleanapplication.dog.data.Dog

/**
 * Created by geuma on 3/5/2018.
 */
/**
 * This POJO holds the response data for the DogApi requests
 *
 * @param data the actual array of dog objects
 * @param count the amount of dog objects in this response
 * @param error an optional error if the service threw one
 */
class DogsResponse(val data: List<Dog>, val count: Int, val error: String?)