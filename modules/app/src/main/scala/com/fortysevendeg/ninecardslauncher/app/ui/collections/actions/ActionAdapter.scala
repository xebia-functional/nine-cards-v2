package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions

import android.support.v7.widget.{GridLayoutManager, RecyclerView}

trait ActionAdapter extends RecyclerView.Adapter[RecyclerView.ViewHolder] {

  def getLayoutManager: GridLayoutManager

}
