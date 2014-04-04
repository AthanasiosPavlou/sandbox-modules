package com.blipnip.admin.client;

import com.google.gwt.user.client.ui.HasWidgets;

/**
 * Since this class is duplicated in each module, move it into commons and inhert it in module you are trying to load.
 * 
 * @author Thanos
 *
 */
public abstract interface Presenter 
{
  public abstract void go(final HasWidgets container);
}
