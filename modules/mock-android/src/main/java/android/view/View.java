package android.view;
public class View
        implements android.graphics.drawable.Drawable.Callback, android.view.KeyEvent.Callback, android.view.accessibility.AccessibilityEventSource
{
    public static interface OnLayoutChangeListener
    {
        public abstract  void onLayoutChange(android.view.View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom);
    }
    public static class DragShadowBuilder
    {
        public  DragShadowBuilder(android.view.View view) { throw new RuntimeException("Stub!"); }
        public  DragShadowBuilder() { throw new RuntimeException("Stub!"); }
        @java.lang.SuppressWarnings(value={"JavadocReference"})
        public final  android.view.View getView() { throw new RuntimeException("Stub!"); }
        public  void onProvideShadowMetrics(android.graphics.Point shadowSize, android.graphics.Point shadowTouchPoint) { throw new RuntimeException("Stub!"); }
        public  void onDrawShadow(android.graphics.Canvas canvas) { throw new RuntimeException("Stub!"); }
    }
    public static class MeasureSpec
    {
        public  MeasureSpec() { throw new RuntimeException("Stub!"); }
        public static  int makeMeasureSpec(int size, int mode) { throw new RuntimeException("Stub!"); }
        public static  int getMode(int measureSpec) { throw new RuntimeException("Stub!"); }
        public static  int getSize(int measureSpec) { throw new RuntimeException("Stub!"); }
        public static  java.lang.String toString(int measureSpec) { throw new RuntimeException("Stub!"); }
        public static final int UNSPECIFIED = 0;
        public static final int EXACTLY = 1073741824;
        public static final int AT_MOST = -2147483648;
    }
    public static interface OnKeyListener
    {
        public abstract  boolean onKey(android.view.View v, int keyCode, android.view.KeyEvent event);
    }
    public static interface OnTouchListener
    {
        public abstract  boolean onTouch(android.view.View v, android.view.MotionEvent event);
    }
    public static interface OnHoverListener
    {
        public abstract  boolean onHover(android.view.View v, android.view.MotionEvent event);
    }
    public static interface OnGenericMotionListener
    {
        public abstract  boolean onGenericMotion(android.view.View v, android.view.MotionEvent event);
    }
    public static interface OnLongClickListener
    {
        public abstract  boolean onLongClick(android.view.View v);
    }
    public static interface OnDragListener
    {
        public abstract  boolean onDrag(android.view.View v, android.view.DragEvent event);
    }
    public static interface OnFocusChangeListener
    {
        public abstract  void onFocusChange(android.view.View v, boolean hasFocus);
    }
    public static interface OnClickListener
    {
        public abstract  void onClick(android.view.View v);
    }
    public static interface OnCreateContextMenuListener
    {
        public abstract  void onCreateContextMenu(android.view.ContextMenu menu, android.view.View v, android.view.ContextMenu.ContextMenuInfo menuInfo);
    }
    public static interface OnSystemUiVisibilityChangeListener
    {
        public abstract  void onSystemUiVisibilityChange(int visibility);
    }
    public static interface OnAttachStateChangeListener
    {
        public abstract  void onViewAttachedToWindow(android.view.View v);
        public abstract  void onViewDetachedFromWindow(android.view.View v);
    }
    public static class BaseSavedState
            extends android.view.AbsSavedState
    {
        public  BaseSavedState(android.os.Parcel source) { super((android.os.Parcel)null); throw new RuntimeException("Stub!"); }
        public  BaseSavedState(android.os.Parcelable superState) { super((android.os.Parcel)null); throw new RuntimeException("Stub!"); }
        public static final android.os.Parcelable.Creator<android.view.View.BaseSavedState> CREATOR;
        static { CREATOR = null; }
    }
    public static class AccessibilityDelegate
    {
        public  AccessibilityDelegate() { throw new RuntimeException("Stub!"); }
        public  void sendAccessibilityEvent(android.view.View host, int eventType) { throw new RuntimeException("Stub!"); }
        public  boolean performAccessibilityAction(android.view.View host, int action, android.os.Bundle args) { throw new RuntimeException("Stub!"); }
        public  void sendAccessibilityEventUnchecked(android.view.View host, android.view.accessibility.AccessibilityEvent event) { throw new RuntimeException("Stub!"); }
        public  boolean dispatchPopulateAccessibilityEvent(android.view.View host, android.view.accessibility.AccessibilityEvent event) { throw new RuntimeException("Stub!"); }
        public  void onPopulateAccessibilityEvent(android.view.View host, android.view.accessibility.AccessibilityEvent event) { throw new RuntimeException("Stub!"); }
        public  void onInitializeAccessibilityEvent(android.view.View host, android.view.accessibility.AccessibilityEvent event) { throw new RuntimeException("Stub!"); }
        public  void onInitializeAccessibilityNodeInfo(android.view.View host, android.view.accessibility.AccessibilityNodeInfo info) { throw new RuntimeException("Stub!"); }
        public  boolean onRequestSendAccessibilityEvent(android.view.ViewGroup host, android.view.View child, android.view.accessibility.AccessibilityEvent event) { throw new RuntimeException("Stub!"); }
        public  android.view.accessibility.AccessibilityNodeProvider getAccessibilityNodeProvider(android.view.View host) { throw new RuntimeException("Stub!"); }
    }
    public  View(android.content.Context context) { throw new RuntimeException("Stub!"); }
    public  View(android.content.Context context, android.util.AttributeSet attrs) { throw new RuntimeException("Stub!"); }
    public  View(android.content.Context context, android.util.AttributeSet attrs, int defStyle) { throw new RuntimeException("Stub!"); }
    protected  void initializeFadingEdge(android.content.res.TypedArray a) { throw new RuntimeException("Stub!"); }
    public  int getVerticalFadingEdgeLength() { throw new RuntimeException("Stub!"); }
    public  void setFadingEdgeLength(int length) { throw new RuntimeException("Stub!"); }
    public  int getHorizontalFadingEdgeLength() { throw new RuntimeException("Stub!"); }
    public  int getVerticalScrollbarWidth() { throw new RuntimeException("Stub!"); }
    protected  int getHorizontalScrollbarHeight() { throw new RuntimeException("Stub!"); }
    protected  void initializeScrollbars(android.content.res.TypedArray a) { throw new RuntimeException("Stub!"); }
    public  void setVerticalScrollbarPosition(int position) { throw new RuntimeException("Stub!"); }
    public  int getVerticalScrollbarPosition() { throw new RuntimeException("Stub!"); }
    public  void setOnFocusChangeListener(android.view.View.OnFocusChangeListener l) { throw new RuntimeException("Stub!"); }
    public  void addOnLayoutChangeListener(android.view.View.OnLayoutChangeListener listener) { throw new RuntimeException("Stub!"); }
    public  void removeOnLayoutChangeListener(android.view.View.OnLayoutChangeListener listener) { throw new RuntimeException("Stub!"); }
    public  void addOnAttachStateChangeListener(android.view.View.OnAttachStateChangeListener listener) { throw new RuntimeException("Stub!"); }
    public  void removeOnAttachStateChangeListener(android.view.View.OnAttachStateChangeListener listener) { throw new RuntimeException("Stub!"); }
    public  android.view.View.OnFocusChangeListener getOnFocusChangeListener() { throw new RuntimeException("Stub!"); }
    public  void setOnClickListener(android.view.View.OnClickListener l) { throw new RuntimeException("Stub!"); }
    public  boolean hasOnClickListeners() { throw new RuntimeException("Stub!"); }
    public  void setOnLongClickListener(android.view.View.OnLongClickListener l) { throw new RuntimeException("Stub!"); }
    public  void setOnCreateContextMenuListener(android.view.View.OnCreateContextMenuListener l) { throw new RuntimeException("Stub!"); }
    public  boolean performClick() { throw new RuntimeException("Stub!"); }
    public  boolean callOnClick() { throw new RuntimeException("Stub!"); }
    public  boolean performLongClick() { throw new RuntimeException("Stub!"); }
    public  boolean showContextMenu() { throw new RuntimeException("Stub!"); }
    public  android.view.ActionMode startActionMode(android.view.ActionMode.Callback callback) { throw new RuntimeException("Stub!"); }
    public  void setOnKeyListener(android.view.View.OnKeyListener l) { throw new RuntimeException("Stub!"); }
    public  void setOnTouchListener(android.view.View.OnTouchListener l) { throw new RuntimeException("Stub!"); }
    public  void setOnGenericMotionListener(android.view.View.OnGenericMotionListener l) { throw new RuntimeException("Stub!"); }
    public  void setOnHoverListener(android.view.View.OnHoverListener l) { throw new RuntimeException("Stub!"); }
    public  void setOnDragListener(android.view.View.OnDragListener l) { throw new RuntimeException("Stub!"); }
    public  boolean requestRectangleOnScreen(android.graphics.Rect rectangle) { throw new RuntimeException("Stub!"); }
    public  boolean requestRectangleOnScreen(android.graphics.Rect rectangle, boolean immediate) { throw new RuntimeException("Stub!"); }
    public  void clearFocus() { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="focus")
    public  boolean hasFocus() { throw new RuntimeException("Stub!"); }
    public  boolean hasFocusable() { throw new RuntimeException("Stub!"); }
    protected  void onFocusChanged(boolean gainFocus, int direction, android.graphics.Rect previouslyFocusedRect) { throw new RuntimeException("Stub!"); }
    public  void sendAccessibilityEvent(int eventType) { throw new RuntimeException("Stub!"); }
    public  void announceForAccessibility(java.lang.CharSequence text) { throw new RuntimeException("Stub!"); }
    public  void sendAccessibilityEventUnchecked(android.view.accessibility.AccessibilityEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean dispatchPopulateAccessibilityEvent(android.view.accessibility.AccessibilityEvent event) { throw new RuntimeException("Stub!"); }
    public  void onPopulateAccessibilityEvent(android.view.accessibility.AccessibilityEvent event) { throw new RuntimeException("Stub!"); }
    public  void onInitializeAccessibilityEvent(android.view.accessibility.AccessibilityEvent event) { throw new RuntimeException("Stub!"); }
    public  android.view.accessibility.AccessibilityNodeInfo createAccessibilityNodeInfo() { throw new RuntimeException("Stub!"); }
    public  void onInitializeAccessibilityNodeInfo(android.view.accessibility.AccessibilityNodeInfo info) { throw new RuntimeException("Stub!"); }
    public  void setAccessibilityDelegate(android.view.View.AccessibilityDelegate delegate) { throw new RuntimeException("Stub!"); }
    public  android.view.accessibility.AccessibilityNodeProvider getAccessibilityNodeProvider() { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="accessibility")
    public  java.lang.CharSequence getContentDescription() { throw new RuntimeException("Stub!"); }
    public  void setContentDescription(java.lang.CharSequence contentDescription) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="focus")
    public  boolean isFocused() { throw new RuntimeException("Stub!"); }
    public  android.view.View findFocus() { throw new RuntimeException("Stub!"); }
    public  boolean isScrollContainer() { throw new RuntimeException("Stub!"); }
    public  void setScrollContainer(boolean isScrollContainer) { throw new RuntimeException("Stub!"); }
    public  int getDrawingCacheQuality() { throw new RuntimeException("Stub!"); }
    public  void setDrawingCacheQuality(int quality) { throw new RuntimeException("Stub!"); }
    public  boolean getKeepScreenOn() { throw new RuntimeException("Stub!"); }
    public  void setKeepScreenOn(boolean keepScreenOn) { throw new RuntimeException("Stub!"); }
    public  int getNextFocusLeftId() { throw new RuntimeException("Stub!"); }
    public  void setNextFocusLeftId(int nextFocusLeftId) { throw new RuntimeException("Stub!"); }
    public  int getNextFocusRightId() { throw new RuntimeException("Stub!"); }
    public  void setNextFocusRightId(int nextFocusRightId) { throw new RuntimeException("Stub!"); }
    public  int getNextFocusUpId() { throw new RuntimeException("Stub!"); }
    public  void setNextFocusUpId(int nextFocusUpId) { throw new RuntimeException("Stub!"); }
    public  int getNextFocusDownId() { throw new RuntimeException("Stub!"); }
    public  void setNextFocusDownId(int nextFocusDownId) { throw new RuntimeException("Stub!"); }
    public  int getNextFocusForwardId() { throw new RuntimeException("Stub!"); }
    public  void setNextFocusForwardId(int nextFocusForwardId) { throw new RuntimeException("Stub!"); }
    public  boolean isShown() { throw new RuntimeException("Stub!"); }
    protected  boolean fitSystemWindows(android.graphics.Rect insets) { throw new RuntimeException("Stub!"); }
    public  void setFitsSystemWindows(boolean fitSystemWindows) { throw new RuntimeException("Stub!"); }
    public  boolean getFitsSystemWindows() { throw new RuntimeException("Stub!"); }
    public  void requestFitSystemWindows() { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(mapping={@android.view.ViewDebug.IntToString(from=0,to="VISIBLE"),@android.view.ViewDebug.IntToString(from=4,to="INVISIBLE"),@android.view.ViewDebug.IntToString(from=8,to="GONE")})
    public  int getVisibility() { throw new RuntimeException("Stub!"); }
    public  void setVisibility(int visibility) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty()
    public  boolean isEnabled() { throw new RuntimeException("Stub!"); }
    public  void setEnabled(boolean enabled) { throw new RuntimeException("Stub!"); }
    public  void setFocusable(boolean focusable) { throw new RuntimeException("Stub!"); }
    public  void setFocusableInTouchMode(boolean focusableInTouchMode) { throw new RuntimeException("Stub!"); }
    public  void setSoundEffectsEnabled(boolean soundEffectsEnabled) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty()
    public  boolean isSoundEffectsEnabled() { throw new RuntimeException("Stub!"); }
    public  void setHapticFeedbackEnabled(boolean hapticFeedbackEnabled) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty()
    public  boolean isHapticFeedbackEnabled() { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="layout")
    public  boolean hasTransientState() { throw new RuntimeException("Stub!"); }
    public  void setHasTransientState(boolean hasTransientState) { throw new RuntimeException("Stub!"); }
    public  void setWillNotDraw(boolean willNotDraw) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  boolean willNotDraw() { throw new RuntimeException("Stub!"); }
    public  void setWillNotCacheDrawing(boolean willNotCacheDrawing) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  boolean willNotCacheDrawing() { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty()
    public  boolean isClickable() { throw new RuntimeException("Stub!"); }
    public  void setClickable(boolean clickable) { throw new RuntimeException("Stub!"); }
    public  boolean isLongClickable() { throw new RuntimeException("Stub!"); }
    public  void setLongClickable(boolean longClickable) { throw new RuntimeException("Stub!"); }
    public  void setPressed(boolean pressed) { throw new RuntimeException("Stub!"); }
    protected  void dispatchSetPressed(boolean pressed) { throw new RuntimeException("Stub!"); }
    public  boolean isPressed() { throw new RuntimeException("Stub!"); }
    public  boolean isSaveEnabled() { throw new RuntimeException("Stub!"); }
    public  void setSaveEnabled(boolean enabled) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty()
    public  boolean getFilterTouchesWhenObscured() { throw new RuntimeException("Stub!"); }
    public  void setFilterTouchesWhenObscured(boolean enabled) { throw new RuntimeException("Stub!"); }
    public  boolean isSaveFromParentEnabled() { throw new RuntimeException("Stub!"); }
    public  void setSaveFromParentEnabled(boolean enabled) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="focus")
    public final  boolean isFocusable() { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty()
    public final  boolean isFocusableInTouchMode() { throw new RuntimeException("Stub!"); }
    public  android.view.View focusSearch(int direction) { throw new RuntimeException("Stub!"); }
    public  boolean dispatchUnhandledMove(android.view.View focused, int direction) { throw new RuntimeException("Stub!"); }
    public  java.util.ArrayList<android.view.View> getFocusables(int direction) { throw new RuntimeException("Stub!"); }
    public  void addFocusables(java.util.ArrayList<android.view.View> views, int direction) { throw new RuntimeException("Stub!"); }
    public  void addFocusables(java.util.ArrayList<android.view.View> views, int direction, int focusableMode) { throw new RuntimeException("Stub!"); }
    public  void findViewsWithText(java.util.ArrayList<android.view.View> outViews, java.lang.CharSequence searched, int flags) { throw new RuntimeException("Stub!"); }
    public  java.util.ArrayList<android.view.View> getTouchables() { throw new RuntimeException("Stub!"); }
    public  void addTouchables(java.util.ArrayList<android.view.View> views) { throw new RuntimeException("Stub!"); }
    public final  boolean requestFocus() { throw new RuntimeException("Stub!"); }
    public final  boolean requestFocus(int direction) { throw new RuntimeException("Stub!"); }
    public  boolean requestFocus(int direction, android.graphics.Rect previouslyFocusedRect) { throw new RuntimeException("Stub!"); }
    public final  boolean requestFocusFromTouch() { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="accessibility",mapping={@android.view.ViewDebug.IntToString(from=0,to="auto"),@android.view.ViewDebug.IntToString(from=1,to="yes"),@android.view.ViewDebug.IntToString(from=2,to="no")})
    public  int getImportantForAccessibility() { throw new RuntimeException("Stub!"); }
    public  void setImportantForAccessibility(int mode) { throw new RuntimeException("Stub!"); }
    public  android.view.ViewParent getParentForAccessibility() { throw new RuntimeException("Stub!"); }
    public  void addChildrenForAccessibility(java.util.ArrayList<android.view.View> children) { throw new RuntimeException("Stub!"); }
    public  boolean performAccessibilityAction(int action, android.os.Bundle arguments) { throw new RuntimeException("Stub!"); }
    public  void onStartTemporaryDetach() { throw new RuntimeException("Stub!"); }
    public  void onFinishTemporaryDetach() { throw new RuntimeException("Stub!"); }
    public  android.view.KeyEvent.DispatcherState getKeyDispatcherState() { throw new RuntimeException("Stub!"); }
    public  boolean dispatchKeyEventPreIme(android.view.KeyEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean dispatchKeyEvent(android.view.KeyEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean dispatchKeyShortcutEvent(android.view.KeyEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean dispatchTouchEvent(android.view.MotionEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean onFilterTouchEventForSecurity(android.view.MotionEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean dispatchTrackballEvent(android.view.MotionEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean dispatchGenericMotionEvent(android.view.MotionEvent event) { throw new RuntimeException("Stub!"); }
    protected  boolean dispatchHoverEvent(android.view.MotionEvent event) { throw new RuntimeException("Stub!"); }
    protected  boolean dispatchGenericPointerEvent(android.view.MotionEvent event) { throw new RuntimeException("Stub!"); }
    protected  boolean dispatchGenericFocusedEvent(android.view.MotionEvent event) { throw new RuntimeException("Stub!"); }
    public  void dispatchWindowFocusChanged(boolean hasFocus) { throw new RuntimeException("Stub!"); }
    public  void onWindowFocusChanged(boolean hasWindowFocus) { throw new RuntimeException("Stub!"); }
    public  boolean hasWindowFocus() { throw new RuntimeException("Stub!"); }
    protected  void dispatchVisibilityChanged(android.view.View changedView, int visibility) { throw new RuntimeException("Stub!"); }
    protected  void onVisibilityChanged(android.view.View changedView, int visibility) { throw new RuntimeException("Stub!"); }
    public  void dispatchDisplayHint(int hint) { throw new RuntimeException("Stub!"); }
    protected  void onDisplayHint(int hint) { throw new RuntimeException("Stub!"); }
    public  void dispatchWindowVisibilityChanged(int visibility) { throw new RuntimeException("Stub!"); }
    protected  void onWindowVisibilityChanged(int visibility) { throw new RuntimeException("Stub!"); }
    public  int getWindowVisibility() { throw new RuntimeException("Stub!"); }
    public  void getWindowVisibleDisplayFrame(android.graphics.Rect outRect) { throw new RuntimeException("Stub!"); }
    public  void dispatchConfigurationChanged(android.content.res.Configuration newConfig) { throw new RuntimeException("Stub!"); }
    protected  void onConfigurationChanged(android.content.res.Configuration newConfig) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty()
    public  boolean isInTouchMode() { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.CapturedViewProperty()
    public final  android.content.Context getContext() { throw new RuntimeException("Stub!"); }
    public  boolean onKeyPreIme(int keyCode, android.view.KeyEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean onKeyDown(int keyCode, android.view.KeyEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean onKeyLongPress(int keyCode, android.view.KeyEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean onKeyUp(int keyCode, android.view.KeyEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean onKeyMultiple(int keyCode, int repeatCount, android.view.KeyEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean onKeyShortcut(int keyCode, android.view.KeyEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean onCheckIsTextEditor() { throw new RuntimeException("Stub!"); }
    public  android.view.inputmethod.InputConnection onCreateInputConnection(android.view.inputmethod.EditorInfo outAttrs) { throw new RuntimeException("Stub!"); }
    public  boolean checkInputConnectionProxy(android.view.View view) { throw new RuntimeException("Stub!"); }
    public  void createContextMenu(android.view.ContextMenu menu) { throw new RuntimeException("Stub!"); }
    protected  android.view.ContextMenu.ContextMenuInfo getContextMenuInfo() { throw new RuntimeException("Stub!"); }
    protected  void onCreateContextMenu(android.view.ContextMenu menu) { throw new RuntimeException("Stub!"); }
    public  boolean onTrackballEvent(android.view.MotionEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean onGenericMotionEvent(android.view.MotionEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean onHoverEvent(android.view.MotionEvent event) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty()
    public  boolean isHovered() { throw new RuntimeException("Stub!"); }
    public  void setHovered(boolean hovered) { throw new RuntimeException("Stub!"); }
    public  void onHoverChanged(boolean hovered) { throw new RuntimeException("Stub!"); }
    public  boolean onTouchEvent(android.view.MotionEvent event) { throw new RuntimeException("Stub!"); }
    public  void cancelLongPress() { throw new RuntimeException("Stub!"); }
    public  void setTouchDelegate(android.view.TouchDelegate delegate) { throw new RuntimeException("Stub!"); }
    public  android.view.TouchDelegate getTouchDelegate() { throw new RuntimeException("Stub!"); }
    public  void bringToFront() { throw new RuntimeException("Stub!"); }
    protected  void onScrollChanged(int l, int t, int oldl, int oldt) { throw new RuntimeException("Stub!"); }
    protected  void onSizeChanged(int w, int h, int oldw, int oldh) { throw new RuntimeException("Stub!"); }
    protected  void dispatchDraw(android.graphics.Canvas canvas) { throw new RuntimeException("Stub!"); }
    public final  android.view.ViewParent getParent() { throw new RuntimeException("Stub!"); }
    public  void setScrollX(int value) { throw new RuntimeException("Stub!"); }
    public  void setScrollY(int value) { throw new RuntimeException("Stub!"); }
    public final  int getScrollX() { throw new RuntimeException("Stub!"); }
    public final  int getScrollY() { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="layout")
    public final  int getWidth() { return 0; }
    @android.view.ViewDebug.ExportedProperty(category="layout")
    public final  int getHeight() { return 0; }
    public  void getDrawingRect(android.graphics.Rect outRect) { throw new RuntimeException("Stub!"); }
    public final  int getMeasuredWidth() { throw new RuntimeException("Stub!"); }
    public final  int getMeasuredWidthAndState() { throw new RuntimeException("Stub!"); }
    public final  int getMeasuredHeight() { throw new RuntimeException("Stub!"); }
    public final  int getMeasuredHeightAndState() { throw new RuntimeException("Stub!"); }
    public final  int getMeasuredState() { throw new RuntimeException("Stub!"); }
    public  android.graphics.Matrix getMatrix() { throw new RuntimeException("Stub!"); }
    public  float getCameraDistance() { throw new RuntimeException("Stub!"); }
    public  void setCameraDistance(float distance) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  float getRotation() { throw new RuntimeException("Stub!"); }
    public  void setRotation(float rotation) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  float getRotationY() { throw new RuntimeException("Stub!"); }
    public  void setRotationY(float rotationY) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  float getRotationX() { throw new RuntimeException("Stub!"); }
    public  void setRotationX(float rotationX) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  float getScaleX() { throw new RuntimeException("Stub!"); }
    public  void setScaleX(float scaleX) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  float getScaleY() { throw new RuntimeException("Stub!"); }
    public  void setScaleY(float scaleY) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  float getPivotX() { throw new RuntimeException("Stub!"); }
    public  void setPivotX(float pivotX) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  float getPivotY() { throw new RuntimeException("Stub!"); }
    public  void setPivotY(float pivotY) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  float getAlpha() { throw new RuntimeException("Stub!"); }
    public  boolean hasOverlappingRendering() { throw new RuntimeException("Stub!"); }
    public  void setAlpha(float alpha) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.CapturedViewProperty()
    public final  int getTop() { throw new RuntimeException("Stub!"); }
    public final  void setTop(int top) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.CapturedViewProperty()
    public final  int getBottom() { throw new RuntimeException("Stub!"); }
    public  boolean isDirty() { throw new RuntimeException("Stub!"); }
    public final  void setBottom(int bottom) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.CapturedViewProperty()
    public final  int getLeft() { throw new RuntimeException("Stub!"); }
    public final  void setLeft(int left) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.CapturedViewProperty()
    public final  int getRight() { throw new RuntimeException("Stub!"); }
    public final  void setRight(int right) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  float getX() { throw new RuntimeException("Stub!"); }
    public  void setX(float x) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  float getY() { throw new RuntimeException("Stub!"); }
    public  void setY(float y) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  float getTranslationX() { throw new RuntimeException("Stub!"); }
    public  void setTranslationX(float translationX) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  float getTranslationY() { throw new RuntimeException("Stub!"); }
    public  void setTranslationY(float translationY) { throw new RuntimeException("Stub!"); }
    public  void getHitRect(android.graphics.Rect outRect) { throw new RuntimeException("Stub!"); }
    public  void getFocusedRect(android.graphics.Rect r) { throw new RuntimeException("Stub!"); }
    public  boolean getGlobalVisibleRect(android.graphics.Rect r, android.graphics.Point globalOffset) { throw new RuntimeException("Stub!"); }
    public final  boolean getGlobalVisibleRect(android.graphics.Rect r) { throw new RuntimeException("Stub!"); }
    public final  boolean getLocalVisibleRect(android.graphics.Rect r) { throw new RuntimeException("Stub!"); }
    public  void offsetTopAndBottom(int offset) { throw new RuntimeException("Stub!"); }
    public  void offsetLeftAndRight(int offset) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(deepExport=true,prefix="layout_")
    public  android.view.ViewGroup.LayoutParams getLayoutParams() { throw new RuntimeException("Stub!"); }
    public  void setLayoutParams(android.view.ViewGroup.LayoutParams params) { throw new RuntimeException("Stub!"); }
    public  void scrollTo(int x, int y) { throw new RuntimeException("Stub!"); }
    public  void scrollBy(int x, int y) { throw new RuntimeException("Stub!"); }
    protected  boolean awakenScrollBars() { throw new RuntimeException("Stub!"); }
    protected  boolean awakenScrollBars(int startDelay) { throw new RuntimeException("Stub!"); }
    protected  boolean awakenScrollBars(int startDelay, boolean invalidate) { throw new RuntimeException("Stub!"); }
    public  void invalidate(android.graphics.Rect dirty) { throw new RuntimeException("Stub!"); }
    public  void invalidate(int l, int t, int r, int b) { throw new RuntimeException("Stub!"); }
    public  void invalidate() { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  boolean isOpaque() { throw new RuntimeException("Stub!"); }
    public  android.os.Handler getHandler() { throw new RuntimeException("Stub!"); }
    public  boolean post(java.lang.Runnable action) { throw new RuntimeException("Stub!"); }
    public  boolean postDelayed(java.lang.Runnable action, long delayMillis) { throw new RuntimeException("Stub!"); }
    public  void postOnAnimation(java.lang.Runnable action) { throw new RuntimeException("Stub!"); }
    public  void postOnAnimationDelayed(java.lang.Runnable action, long delayMillis) { throw new RuntimeException("Stub!"); }
    public  boolean removeCallbacks(java.lang.Runnable action) { throw new RuntimeException("Stub!"); }
    public  void postInvalidate() { throw new RuntimeException("Stub!"); }
    public  void postInvalidate(int left, int top, int right, int bottom) { throw new RuntimeException("Stub!"); }
    public  void postInvalidateDelayed(long delayMilliseconds) { throw new RuntimeException("Stub!"); }
    public  void postInvalidateDelayed(long delayMilliseconds, int left, int top, int right, int bottom) { throw new RuntimeException("Stub!"); }
    public  void postInvalidateOnAnimation() { throw new RuntimeException("Stub!"); }
    public  void postInvalidateOnAnimation(int left, int top, int right, int bottom) { throw new RuntimeException("Stub!"); }
    public  void computeScroll() { throw new RuntimeException("Stub!"); }
    public  boolean isHorizontalFadingEdgeEnabled() { throw new RuntimeException("Stub!"); }
    public  void setHorizontalFadingEdgeEnabled(boolean horizontalFadingEdgeEnabled) { throw new RuntimeException("Stub!"); }
    public  boolean isVerticalFadingEdgeEnabled() { throw new RuntimeException("Stub!"); }
    public  void setVerticalFadingEdgeEnabled(boolean verticalFadingEdgeEnabled) { throw new RuntimeException("Stub!"); }
    protected  float getTopFadingEdgeStrength() { throw new RuntimeException("Stub!"); }
    protected  float getBottomFadingEdgeStrength() { throw new RuntimeException("Stub!"); }
    protected  float getLeftFadingEdgeStrength() { throw new RuntimeException("Stub!"); }
    protected  float getRightFadingEdgeStrength() { throw new RuntimeException("Stub!"); }
    public  boolean isHorizontalScrollBarEnabled() { throw new RuntimeException("Stub!"); }
    public  void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) { throw new RuntimeException("Stub!"); }
    public  boolean isVerticalScrollBarEnabled() { throw new RuntimeException("Stub!"); }
    public  void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) { throw new RuntimeException("Stub!"); }
    public  void setScrollbarFadingEnabled(boolean fadeScrollbars) { throw new RuntimeException("Stub!"); }
    public  boolean isScrollbarFadingEnabled() { throw new RuntimeException("Stub!"); }
    public  int getScrollBarDefaultDelayBeforeFade() { throw new RuntimeException("Stub!"); }
    public  void setScrollBarDefaultDelayBeforeFade(int scrollBarDefaultDelayBeforeFade) { throw new RuntimeException("Stub!"); }
    public  int getScrollBarFadeDuration() { throw new RuntimeException("Stub!"); }
    public  void setScrollBarFadeDuration(int scrollBarFadeDuration) { throw new RuntimeException("Stub!"); }
    public  int getScrollBarSize() { throw new RuntimeException("Stub!"); }
    public  void setScrollBarSize(int scrollBarSize) { throw new RuntimeException("Stub!"); }
    public  void setScrollBarStyle(int style) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(mapping={@android.view.ViewDebug.IntToString(from=0,to="INSIDE_OVERLAY"),@android.view.ViewDebug.IntToString(from=16777216,to="INSIDE_INSET"),@android.view.ViewDebug.IntToString(from=33554432,to="OUTSIDE_OVERLAY"),@android.view.ViewDebug.IntToString(from=50331648,to="OUTSIDE_INSET")})
    public  int getScrollBarStyle() { throw new RuntimeException("Stub!"); }
    protected  int computeHorizontalScrollRange() { throw new RuntimeException("Stub!"); }
    protected  int computeHorizontalScrollOffset() { throw new RuntimeException("Stub!"); }
    protected  int computeHorizontalScrollExtent() { throw new RuntimeException("Stub!"); }
    protected  int computeVerticalScrollRange() { throw new RuntimeException("Stub!"); }
    protected  int computeVerticalScrollOffset() { throw new RuntimeException("Stub!"); }
    protected  int computeVerticalScrollExtent() { throw new RuntimeException("Stub!"); }
    public  boolean canScrollHorizontally(int direction) { throw new RuntimeException("Stub!"); }
    public  boolean canScrollVertically(int direction) { throw new RuntimeException("Stub!"); }
    protected final  void onDrawScrollBars(android.graphics.Canvas canvas) { throw new RuntimeException("Stub!"); }
    protected  void onDraw(android.graphics.Canvas canvas) { throw new RuntimeException("Stub!"); }
    protected  void onAttachedToWindow() { throw new RuntimeException("Stub!"); }
    public  void onScreenStateChanged(int screenState) { throw new RuntimeException("Stub!"); }
    protected  void onDetachedFromWindow() { throw new RuntimeException("Stub!"); }
    protected  int getWindowAttachCount() { throw new RuntimeException("Stub!"); }
    public  android.os.IBinder getWindowToken() { throw new RuntimeException("Stub!"); }
    public  android.os.IBinder getApplicationWindowToken() { throw new RuntimeException("Stub!"); }
    public  void saveHierarchyState(android.util.SparseArray<android.os.Parcelable> container) { throw new RuntimeException("Stub!"); }
    protected  void dispatchSaveInstanceState(android.util.SparseArray<android.os.Parcelable> container) { throw new RuntimeException("Stub!"); }
    protected  android.os.Parcelable onSaveInstanceState() { throw new RuntimeException("Stub!"); }
    public  void restoreHierarchyState(android.util.SparseArray<android.os.Parcelable> container) { throw new RuntimeException("Stub!"); }
    protected  void dispatchRestoreInstanceState(android.util.SparseArray<android.os.Parcelable> container) { throw new RuntimeException("Stub!"); }
    protected  void onRestoreInstanceState(android.os.Parcelable state) { throw new RuntimeException("Stub!"); }
    public  long getDrawingTime() { throw new RuntimeException("Stub!"); }
    public  void setDuplicateParentStateEnabled(boolean enabled) { throw new RuntimeException("Stub!"); }
    public  boolean isDuplicateParentStateEnabled() { throw new RuntimeException("Stub!"); }
    public  void setLayerType(int layerType, android.graphics.Paint paint) { throw new RuntimeException("Stub!"); }
    public  int getLayerType() { throw new RuntimeException("Stub!"); }
    public  void buildLayer() { throw new RuntimeException("Stub!"); }
    public  void setDrawingCacheEnabled(boolean enabled) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  boolean isDrawingCacheEnabled() { throw new RuntimeException("Stub!"); }
    public  android.graphics.Bitmap getDrawingCache() { throw new RuntimeException("Stub!"); }
    public  android.graphics.Bitmap getDrawingCache(boolean autoScale) { throw new RuntimeException("Stub!"); }
    public  void destroyDrawingCache() { throw new RuntimeException("Stub!"); }
    public  void setDrawingCacheBackgroundColor(int color) { throw new RuntimeException("Stub!"); }
    public  int getDrawingCacheBackgroundColor() { throw new RuntimeException("Stub!"); }
    public  void buildDrawingCache() { throw new RuntimeException("Stub!"); }
    public  void buildDrawingCache(boolean autoScale) { throw new RuntimeException("Stub!"); }
    public  boolean isInEditMode() { throw new RuntimeException("Stub!"); }
    protected  boolean isPaddingOffsetRequired() { throw new RuntimeException("Stub!"); }
    protected  int getLeftPaddingOffset() { throw new RuntimeException("Stub!"); }
    protected  int getRightPaddingOffset() { throw new RuntimeException("Stub!"); }
    protected  int getTopPaddingOffset() { throw new RuntimeException("Stub!"); }
    protected  int getBottomPaddingOffset() { throw new RuntimeException("Stub!"); }
    public  boolean isHardwareAccelerated() { throw new RuntimeException("Stub!"); }
    public  void draw(android.graphics.Canvas canvas) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="drawing")
    public  int getSolidColor() { throw new RuntimeException("Stub!"); }
    public  boolean isLayoutRequested() { throw new RuntimeException("Stub!"); }
    @java.lang.SuppressWarnings(value={"unchecked"})
    public  void layout(int l, int t, int r, int b) { throw new RuntimeException("Stub!"); }
    protected  void onLayout(boolean changed, int left, int top, int right, int bottom) { throw new RuntimeException("Stub!"); }
    protected  void onFinishInflate() { throw new RuntimeException("Stub!"); }
    public  android.content.res.Resources getResources() { throw new RuntimeException("Stub!"); }
    public  void invalidateDrawable(android.graphics.drawable.Drawable drawable) { throw new RuntimeException("Stub!"); }
    public  void scheduleDrawable(android.graphics.drawable.Drawable who, java.lang.Runnable what, long when) { throw new RuntimeException("Stub!"); }
    public  void unscheduleDrawable(android.graphics.drawable.Drawable who, java.lang.Runnable what) { throw new RuntimeException("Stub!"); }
    public  void unscheduleDrawable(android.graphics.drawable.Drawable who) { throw new RuntimeException("Stub!"); }
    protected  boolean verifyDrawable(android.graphics.drawable.Drawable who) { throw new RuntimeException("Stub!"); }
    protected  void drawableStateChanged() { throw new RuntimeException("Stub!"); }
    public  void refreshDrawableState() { throw new RuntimeException("Stub!"); }
    public final  int[] getDrawableState() { throw new RuntimeException("Stub!"); }
    protected  int[] onCreateDrawableState(int extraSpace) { throw new RuntimeException("Stub!"); }
    protected static  int[] mergeDrawableStates(int[] baseState, int[] additionalState) { throw new RuntimeException("Stub!"); }
    public  void jumpDrawablesToCurrentState() { throw new RuntimeException("Stub!"); }
    public  void setBackgroundColor(int color) { throw new RuntimeException("Stub!"); }
    public  void setBackgroundResource(int resid) { throw new RuntimeException("Stub!"); }
    public  void setBackground(android.graphics.drawable.Drawable background) { throw new RuntimeException("Stub!"); }
    @java.lang.Deprecated()
    public  void setBackgroundDrawable(android.graphics.drawable.Drawable background) { throw new RuntimeException("Stub!"); }
    public  android.graphics.drawable.Drawable getBackground() { throw new RuntimeException("Stub!"); }
    public  void setPadding(int left, int top, int right, int bottom) { throw new RuntimeException("Stub!"); }
    public  int getPaddingTop() { throw new RuntimeException("Stub!"); }
    public  int getPaddingBottom() { throw new RuntimeException("Stub!"); }
    public  int getPaddingLeft() { throw new RuntimeException("Stub!"); }
    public  int getPaddingRight() { throw new RuntimeException("Stub!"); }
    public  void setSelected(boolean selected) { throw new RuntimeException("Stub!"); }
    protected  void dispatchSetSelected(boolean selected) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty()
    public  boolean isSelected() { throw new RuntimeException("Stub!"); }
    public  void setActivated(boolean activated) { throw new RuntimeException("Stub!"); }
    protected  void dispatchSetActivated(boolean activated) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty()
    public  boolean isActivated() { throw new RuntimeException("Stub!"); }
    public  android.view.ViewTreeObserver getViewTreeObserver() { throw new RuntimeException("Stub!"); }
    public  android.view.View getRootView() { throw new RuntimeException("Stub!"); }
    public  void getLocationOnScreen(int[] location) { throw new RuntimeException("Stub!"); }
    public  void getLocationInWindow(int[] location) { throw new RuntimeException("Stub!"); }
    public final  android.view.View findViewById(int id) { throw new RuntimeException("Stub!"); }
    public final  android.view.View findViewWithTag(java.lang.Object tag) { throw new RuntimeException("Stub!"); }
    public  void setId(int id) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.CapturedViewProperty()
    public  int getId() { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty()
    public  java.lang.Object getTag() { throw new RuntimeException("Stub!"); }
    public  void setTag(java.lang.Object tag) { throw new RuntimeException("Stub!"); }
    public  java.lang.Object getTag(int key) { throw new RuntimeException("Stub!"); }
    public  void setTag(int key, java.lang.Object tag) { throw new RuntimeException("Stub!"); }
    @android.view.ViewDebug.ExportedProperty(category="layout")
    public  int getBaseline() { throw new RuntimeException("Stub!"); }
    public  void requestLayout() { throw new RuntimeException("Stub!"); }
    public  void forceLayout() { throw new RuntimeException("Stub!"); }
    public final  void measure(int widthMeasureSpec, int heightMeasureSpec) { throw new RuntimeException("Stub!"); }
    protected  void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { throw new RuntimeException("Stub!"); }
    protected final  void setMeasuredDimension(int measuredWidth, int measuredHeight) { throw new RuntimeException("Stub!"); }
    public static  int combineMeasuredStates(int curState, int newState) { throw new RuntimeException("Stub!"); }
    public static  int resolveSize(int size, int measureSpec) { throw new RuntimeException("Stub!"); }
    public static  int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) { throw new RuntimeException("Stub!"); }
    public static  int getDefaultSize(int size, int measureSpec) { throw new RuntimeException("Stub!"); }
    protected  int getSuggestedMinimumHeight() { throw new RuntimeException("Stub!"); }
    protected  int getSuggestedMinimumWidth() { throw new RuntimeException("Stub!"); }
    public  int getMinimumHeight() { throw new RuntimeException("Stub!"); }
    public  void setMinimumHeight(int minHeight) { throw new RuntimeException("Stub!"); }
    public  int getMinimumWidth() { throw new RuntimeException("Stub!"); }
    public  void setMinimumWidth(int minWidth) { throw new RuntimeException("Stub!"); }
    public  android.view.animation.Animation getAnimation() { throw new RuntimeException("Stub!"); }
    public  void startAnimation(android.view.animation.Animation animation) { throw new RuntimeException("Stub!"); }
    public  void clearAnimation() { throw new RuntimeException("Stub!"); }
    public  void setAnimation(android.view.animation.Animation animation) { throw new RuntimeException("Stub!"); }
    protected  void onAnimationStart() { throw new RuntimeException("Stub!"); }
    protected  void onAnimationEnd() { throw new RuntimeException("Stub!"); }
    protected  boolean onSetAlpha(int alpha) { throw new RuntimeException("Stub!"); }
    public  void playSoundEffect(int soundConstant) { throw new RuntimeException("Stub!"); }
    public  boolean performHapticFeedback(int feedbackConstant) { throw new RuntimeException("Stub!"); }
    public  boolean performHapticFeedback(int feedbackConstant, int flags) { throw new RuntimeException("Stub!"); }
    public  void setSystemUiVisibility(int visibility) { throw new RuntimeException("Stub!"); }
    public  int getSystemUiVisibility() { throw new RuntimeException("Stub!"); }
    public  int getWindowSystemUiVisibility() { throw new RuntimeException("Stub!"); }
    public  void onWindowSystemUiVisibilityChanged(int visible) { throw new RuntimeException("Stub!"); }
    public  void dispatchWindowSystemUiVisiblityChanged(int visible) { throw new RuntimeException("Stub!"); }
    public  void setOnSystemUiVisibilityChangeListener(android.view.View.OnSystemUiVisibilityChangeListener l) { throw new RuntimeException("Stub!"); }
    public  void dispatchSystemUiVisibilityChanged(int visibility) { throw new RuntimeException("Stub!"); }
    public final  boolean startDrag(android.content.ClipData data, android.view.View.DragShadowBuilder shadowBuilder, java.lang.Object myLocalState, int flags) { throw new RuntimeException("Stub!"); }
    public  boolean onDragEvent(android.view.DragEvent event) { throw new RuntimeException("Stub!"); }
    public  boolean dispatchDragEvent(android.view.DragEvent event) { throw new RuntimeException("Stub!"); }
    public static  android.view.View inflate(android.content.Context context, int resource, android.view.ViewGroup root) { throw new RuntimeException("Stub!"); }
    @java.lang.SuppressWarnings(value={"UnusedParameters"})
    protected  boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) { throw new RuntimeException("Stub!"); }
    protected  void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) { throw new RuntimeException("Stub!"); }
    public  int getOverScrollMode() { throw new RuntimeException("Stub!"); }
    public  void setOverScrollMode(int overScrollMode) { throw new RuntimeException("Stub!"); }
    public  android.view.ViewPropertyAnimator animate() { throw new RuntimeException("Stub!"); }
    protected static final java.lang.String VIEW_LOG_TAG = "View";
    public static final int NO_ID = -1;
    public static final int VISIBLE = 0;
    public static final int INVISIBLE = 4;
    public static final int GONE = 8;
    public static final int DRAWING_CACHE_QUALITY_LOW = 524288;
    public static final int DRAWING_CACHE_QUALITY_HIGH = 1048576;
    public static final int DRAWING_CACHE_QUALITY_AUTO = 0;
    public static final int SCROLLBARS_INSIDE_OVERLAY = 0;
    public static final int SCROLLBARS_INSIDE_INSET = 16777216;
    public static final int SCROLLBARS_OUTSIDE_OVERLAY = 33554432;
    public static final int SCROLLBARS_OUTSIDE_INSET = 50331648;
    public static final int KEEP_SCREEN_ON = 67108864;
    public static final int SOUND_EFFECTS_ENABLED = 134217728;
    public static final int HAPTIC_FEEDBACK_ENABLED = 268435456;
    public static final int FOCUSABLES_ALL = 0;
    public static final int FOCUSABLES_TOUCH_MODE = 1;
    public static final int FOCUS_BACKWARD = 1;
    public static final int FOCUS_FORWARD = 2;
    public static final int FOCUS_LEFT = 17;
    public static final int FOCUS_UP = 33;
    public static final int FOCUS_RIGHT = 66;
    public static final int FOCUS_DOWN = 130;
    public static final int MEASURED_SIZE_MASK = 16777215;
    public static final int MEASURED_STATE_MASK = -16777216;
    public static final int MEASURED_HEIGHT_STATE_SHIFT = 16;
    public static final int MEASURED_STATE_TOO_SMALL = 16777216;
    protected static final int[] EMPTY_STATE_SET = null;
    protected static final int[] ENABLED_STATE_SET = null;
    protected static final int[] FOCUSED_STATE_SET = null;
    protected static final int[] SELECTED_STATE_SET = null;
    protected static final int[] WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] ENABLED_FOCUSED_STATE_SET = null;
    protected static final int[] ENABLED_SELECTED_STATE_SET = null;
    protected static final int[] ENABLED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] FOCUSED_SELECTED_STATE_SET = null;
    protected static final int[] FOCUSED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] ENABLED_FOCUSED_SELECTED_STATE_SET = null;
    protected static final int[] ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_SELECTED_STATE_SET = null;
    protected static final int[] PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_FOCUSED_SELECTED_STATE_SET = null;
    protected static final int[] PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_ENABLED_STATE_SET = null;
    protected static final int[] PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_ENABLED_SELECTED_STATE_SET = null;
    protected static final int[] PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_ENABLED_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET = null;
    protected static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    public static final int TEXT_ALIGNMENT_INHERIT = 0;
    public static final int TEXT_ALIGNMENT_RESOLVED_DEFAULT = 131072;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_AUTO = 0;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_YES = 1;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_NO = 2;
    public static final int OVER_SCROLL_ALWAYS = 0;
    public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
    public static final int OVER_SCROLL_NEVER = 2;
    public static final int SYSTEM_UI_FLAG_VISIBLE = 0;
    public static final int SYSTEM_UI_FLAG_LOW_PROFILE = 1;
    public static final int SYSTEM_UI_FLAG_HIDE_NAVIGATION = 2;
    public static final int SYSTEM_UI_FLAG_FULLSCREEN = 4;
    public static final int SYSTEM_UI_FLAG_LAYOUT_STABLE = 256;
    public static final int SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION = 512;
    public static final int SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN = 1024;
    @Deprecated
    public static final int STATUS_BAR_HIDDEN = 1;
    @Deprecated
    public static final int STATUS_BAR_VISIBLE = 0;
    public static final int SYSTEM_UI_LAYOUT_FLAGS = 1536;
    public static final int FIND_VIEWS_WITH_TEXT = 1;
    public static final int FIND_VIEWS_WITH_CONTENT_DESCRIPTION = 2;
    public static final int SCREEN_STATE_OFF = 0;
    public static final int SCREEN_STATE_ON = 1;
    public static final int SCROLLBAR_POSITION_DEFAULT = 0;
    public static final int SCROLLBAR_POSITION_LEFT = 1;
    public static final int SCROLLBAR_POSITION_RIGHT = 2;
    public static final int LAYER_TYPE_NONE = 0;
    public static final int LAYER_TYPE_SOFTWARE = 1;
    public static final int LAYER_TYPE_HARDWARE = 2;
    public static final android.util.Property<android.view.View, java.lang.Float> ALPHA;
    public static final android.util.Property<android.view.View, java.lang.Float> TRANSLATION_X;
    public static final android.util.Property<android.view.View, java.lang.Float> TRANSLATION_Y;
    public static final android.util.Property<android.view.View, java.lang.Float> X;
    public static final android.util.Property<android.view.View, java.lang.Float> Y;
    public static final android.util.Property<android.view.View, java.lang.Float> ROTATION;
    public static final android.util.Property<android.view.View, java.lang.Float> ROTATION_X;
    public static final android.util.Property<android.view.View, java.lang.Float> ROTATION_Y;
    public static final android.util.Property<android.view.View, java.lang.Float> SCALE_X;
    public static final android.util.Property<android.view.View, java.lang.Float> SCALE_Y;
    static { ALPHA = null; TRANSLATION_X = null; TRANSLATION_Y = null; X = null; Y = null; ROTATION = null; ROTATION_X = null; ROTATION_Y = null; SCALE_X = null; SCALE_Y = null; }
}
