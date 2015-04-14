package com.fortysevendeg.ninecardslauncher.modules.api.impl

import com.fortysevendeg.rest.client.http.OkHttpClient
import com.squareup.{okhttp => okHttp}

class OkHttpClientImpl extends OkHttpClient {

  override val okHttpClient = new okHttp.OkHttpClient

}
