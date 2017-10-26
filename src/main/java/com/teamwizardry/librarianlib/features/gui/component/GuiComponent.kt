package com.teamwizardry.librarianlib.features.gui.component

import com.teamwizardry.librarianlib.core.LibrarianLib
import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.animator.Animator
import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventBus
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton
import com.teamwizardry.librarianlib.features.gui.IGuiDrawable
import com.teamwizardry.librarianlib.features.gui.Key
import com.teamwizardry.librarianlib.features.gui.Option
import com.teamwizardry.librarianlib.features.gui.component.supporting.ComponentTransform
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Matrix4
import com.teamwizardry.librarianlib.features.math.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.fml.client.config.GuiUtils
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.util.*


/**
 * The base class of every on-screen object. These can be nested within each other using [add]. Subcomponents will be
 * positioned relative to their parent, so modifications to the parent's [pos] will change their rendering position.
 *
 * # Summery
 *
 * - Events - Fire when something happens, allow you to change what happens or cancel it alltogether. Register on [BUS]
 * - Tags - Mark a component for retrieval later.
 * - Data - Store metadata in a component.
 *
 * # Detail
 *
 * ## Events
 *
 * More advanced functionality is achieved through event hooks on the component's [BUS]. All events are subclasses of
 * [Event] so a type hierarchy of that should show all events available to you. Only the child classes of [GuiComponent]
 * are fired by default, all others are either a part of a particular component class or require some action on the
 * user's part to initialize.
 *
 * ## Tags
 *
 * If you want to mark a component for retrieval later you can use [addTag] to add an arbitrary object as a tag.
 * Children with a specific tag can be retrieved later using [getByTag], or you can check if a component has a tag using
 * [hasTag]. Tags are stored in a HashSet, so any object that overrides the [hashCode] and [equals] methods will work by
 * value, but any object will work by identity. [Explanation here.](http://stackoverflow.com/a/1692882/1541907)
 *
 * ## Data
 *
 * If you need to store additional metadata in a component, this can be done with [setData]. The class passed in must be
 * the class of the data, and is used to reduce unchecked cast warnings and to ensure that the same key can be used with
 * multiple types of data. The key is used to allow multiple instances of the same data type to be stored in a component,
 * and is independent per class.
 * ```
 * component.setData(MyCoolObject.class, "foo", myInstance);
 * component.setData(MyCoolObject.class, "bar", anotherInstance);
 * component.setData(YourCoolObject.class, "foo", yourInstance);
 *
 * component.getData(MyCoolObject.class, "foo"); // => myInstance
 * component.getData(MyCoolObject.class, "bar"); // => anotherInstance
 * component.getData(YourCoolObject.class, "foo"); // => yourInstance
 * ```
 *
 */
@SideOnly(Side.CLIENT)
abstract class GuiComponent @JvmOverloads constructor(posX: Int, posY: Int, width: Int = 0, height: Int = 0) : IGuiDrawable {

    @JvmField
    val BUS = EventBus()

    var animator: Animator
        get() {
            var a = animatorStorage ?: parent?.animator
            if(a == null) {
                a = Animator()
                animatorStorage = a
            }
            return a
        }
        set(value) {
            animatorStorage = value
        }

    private var animatorStorage: Animator? = null

    var zIndex = 0
    var transform = ComponentTransform()
    /**
     * The size of the component
     */
    var size: Vec2d

    /**
     * The position of the component relative to its parent
     */
    var pos: Vec2d
        get() = transform.translate
        set(value) { transform.translate = value }

    var mouseOver = false
    var mouseOverNoOcclusion = false

    var mousePosThisFrame = Vec2d.ZERO
    protected var tagStorage: MutableSet<Any> = HashSet()
    /**
     * Do not use this to check if a component has a tag, as event hooks can add virtual tags to components. Use [hasTag] instead.
     *
     * Returns an unmodifiable set of all the tags this component has.
     *
     * You should use [addTag] and [removeTag] to modify the tag set.
     */
    fun getTags() = Collections.unmodifiableSet<Any>(tagStorage)!!

    /**
     * Whether this component should be drawn or have events fire
     */
    var isVisible = true
    /**
     * Returns true if this component is invalid and it should be removed from its parent
     * @return
     */
    var isInvalid = false
        protected set

    protected var mouseButtonsDown = BooleanArray(EnumMouseButton.values().size)
    protected var keysDown: MutableMap<Key, Boolean> = HashMap<Key, Boolean>().withDefault({ false })
    private val data: MutableMap<Class<*>, MutableMap<String, Any>> = mutableMapOf()

    var tooltip: Option<GuiComponent, List<String>?> = Option(null)
    var tooltipFont: FontRenderer? = null

    /**
     * Set whether the element should calculate hovering based on it's bounds as
     * well as it's children or if it should only calculate based on it's children.
     */
    var calculateOwnHover = true
    /**
     * True if the component shouldn't effect the logical size of it's parent. Causes logical size to return null.
     */
    var outOfFlow = false
    protected val components = mutableListOf<GuiComponent>()
    /**
     * An unmodifiable collection of all the direct children of this component
     */
    val children: Collection<GuiComponent> = Collections.unmodifiableCollection(components)
    /**
     * An unmodifiable collection of all the children of this component, recursively.
     */
    val allChildren: Collection<GuiComponent>
        get() {
            val list = mutableListOf<GuiComponent>()
            addChildrenRecursively(list)
            return Collections.unmodifiableCollection(list)
        }

    private fun addChildrenRecursively(list: MutableList<GuiComponent>) {
        list.addAll(components)
        components.forEach { it.addChildrenRecursively(list) }
    }

    var parent: GuiComponent? = null
        private set(value) {
            parents.clear()
            if (value != null) {
                parents.addAll(value.parents)
                parents.add(value)
            }
            field = value
        }

    var parents: LinkedHashSet<GuiComponent> = LinkedHashSet()

    init {
        this.pos = vec(posX, posY)
        this.size = vec(width, height)
    }

    /**
     * Draws the component, this is called between pre and post draw events
     */
    abstract fun drawComponent(mousePos: Vec2d, partialTicks: Float)

    /**
     * Adds child(ren) to this component.

     * @throws IllegalArgumentException if the component had a parent already
     */
    fun add(vararg components: GuiComponent?) {
        components.forEach { addInternal(it) }
    }

    protected fun addInternal(component: GuiComponent?) {
        if (component == null) {
            LibrarianLog.error("Null component, ignoring")
            return
        }
        if (component === this)
            throw IllegalArgumentException("Immediately recursive component hierarchy")

        if (component.parent != null) {
            if (component.parent == this) {
                LibrarianLog.warn("You tried to add the component to the same parent twice. Why?")
                return
            } else {
                throw IllegalArgumentException("Component already had a parent")
            }
        }

        if (component in parents) {
            throw IllegalArgumentException("Recursive component hierarchy")
        }


        if (BUS.fire(GuiComponentEvents.AddChildEvent(this, component)).isCanceled())
            return
        if (component.BUS.fire(GuiComponentEvents.AddToParentEvent(component, this)).isCanceled())
            return
        components.add(component)
        component.parent = this
    }

    operator fun contains(component: GuiComponent): Boolean =
            component in components || components.any { component in it }

    /**
     * Removes the supplied component
     * @param component
     */
    fun remove(component: GuiComponent) {
        if (component !in components)
            return
        if (BUS.fire(GuiComponentEvents.RemoveChildEvent(this, component)).isCanceled())
            return
        if (component.BUS.fire(GuiComponentEvents.RemoveFromParentEvent(component, this)).isCanceled())
            return
        component.parent = null
        components.remove(component)
    }

    /**
     * Removes all components that have the supplied tag
     */
    fun removeByTag(tag: Any) {
        components.removeAll { e ->
            var b = e.hasTag(tag)
            if (BUS.fire(GuiComponentEvents.RemoveChildEvent(this, e)).isCanceled())
                b = false
            if (e.BUS.fire(GuiComponentEvents.RemoveFromParentEvent(e, this)).isCanceled())
                b = false
            if (b) {
                e.parent = null
            }
            b
        }
    }

    /**
     * Iterates over children while allowing children to be added or removed.
     */
    fun forEachChild(l: (GuiComponent) -> Unit) {
        val copy = components.toList()
        copy.forEach(l)
    }

    /**
     * Returns a list of all children that have the tag [tag]
     */
    fun getByTag(tag: Any): List<GuiComponent> {
        val list = mutableListOf<GuiComponent>()
        addByTag(tag, list)
        return list
    }

    /**
     * Returns a list of all children and grandchildren etc. that have the tag [tag]
     */
    fun getAllByTag(tag: Any): List<GuiComponent> {
        val list = mutableListOf<GuiComponent>()
        addAllByTag(tag, list)
        return list
    }

    protected fun addAllByTag(tag: Any, list: MutableList<GuiComponent>) {
        addByTag(tag, list)
        components.forEach { it.addAllByTag(tag, list) }
    }

    protected fun addByTag(tag: Any, list: MutableList<GuiComponent>) {
        components.filterTo(list) { it.hasTag(tag) }
    }

    /**
     * Returns a list of all children that are subclasses of [clazz]
     */
    fun <C : GuiComponent> getByClass(clazz: Class<C>): List<C> {
        val list = mutableListOf<C>()
        addByClass(clazz, list)
        return list
    }

    /**
     * Returns a list of all children and grandchildren etc. that are subclasses of [clazz]
     */
    fun <C : GuiComponent> getAllByClass(clazz: Class<C>): List<C> {
        val list = mutableListOf<C>()
        addAllByClass(clazz, list)
        return list
    }

    protected fun <C : GuiComponent> addAllByClass(clazz: Class<C>, list: MutableList<C>) {
        addByClass(clazz, list)
        components.forEach { it.addAllByClass(clazz, list) }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <C : GuiComponent> addByClass(clazz: Class<C>, list: MutableList<C>) {
        forEachChild { component ->
            if (clazz.isAssignableFrom(component.javaClass))
                list.add(component as C)
        }
    }

    //=============================================================================
    /* Events/checks */
    //=============================================================================

    /**
     * Takes [pos], which is in our parent's context (coordinate space), and transforms it to our context
     */
    fun transformFromParentContext(pos: Vec2d): Vec2d {
        return transform.applyInverse(pos)
    }

    /**
     * Takes [pos], which is in our context (coordinate space), and transforms it to our parent's context
     *
     * [pos] defaults to (0, 0)
     */
    @JvmOverloads
    fun transformToParentContext(pos: Vec2d = Vec2d.ZERO): Vec2d {
        return transform.apply(pos)
    }

    /**
     * Create a matrix that moves coordinates from [other]'s context (coordinate space) to this component's context
     *
     * If [other] is null the returned matrix moves coordinates from the root context to this component's context
     */
    fun otherContextToThisContext(other: GuiComponent?): Matrix4 {
        if(other == null)
            return thisContextToOtherContext(null).invert()
        return other.thisContextToOtherContext(this)
    }

    /**
     * Create a matrix that moves coordinates from this component's context (coordinate space) to [other]'s context
     *
     * If [other] is null the returned matrix moves coordinates from this component's context to the root context
     */
    fun thisContextToOtherContext(other: GuiComponent?): Matrix4 {
        return _thisContextToOtherContext(other, Matrix4())
    }
    private fun _thisContextToOtherContext(other: GuiComponent?, matrix: Matrix4): Matrix4 {
        if(other == null) {
            parent?._thisContextToOtherContext(null, matrix)
            transform.apply(matrix)
            return matrix
        }
        val mat = other.thisContextToOtherContext(null).invert()
        mat *= thisContextToOtherContext(null)
        return mat
    }

    /**
     * A shorthand to transform the passed pos in this component's context (coordinate space) to a pos in [other]'s context
     *
     * If [other] is null the returned value is in the root context
     *
     * [pos] defaults to (0, 0)
     */
    @JvmOverloads
    fun thisPosToOtherContext(other: GuiComponent?, pos: Vec2d = Vec2d.ZERO): Vec2d {
        return thisContextToOtherContext(other) * pos
    }

    open fun calculateMouseOver(mousePos: Vec2d) {
        val mousePos = transformFromParentContext(mousePos)
        this.mouseOver = false

        if (isVisible) {
            components.asReversed().forEach { child ->
                child.calculateMouseOver(mousePos)
                if (mouseOver) {
                    child.mouseOver = false // occlusion
                }
                if (child.mouseOver) {
                    mouseOver = true // mouseover upward transfer
                }

            }

            mouseOver = mouseOver || (calculateOwnHover && calculateOwnHover(mousePos))
        }
        this.mouseOver = BUS.fire(GuiComponentEvents.MouseOverEvent(this, mousePos, this.mouseOver)).isOver
        this.mouseOverNoOcclusion = this.mouseOver
    }

    /**
     * Override this to change the shape of a hover. For instance making a per-pixel sprite hover
     */
    open fun calculateOwnHover(mousePos: Vec2d): Boolean {
        return mousePos.x >= 0 && mousePos.x <= size.x && mousePos.y >= 0 && mousePos.y <= size.y
    }

    private var wasMouseOver = false

    /**
     * Draw this component, don't override in subclasses unless you know what you're doing.
     *
     * @param mousePos Mouse position relative to the position of this component
     * @param partialTicks From 0-1 the additional fractional ticks, used for smooth animations that aren't dependant on wall-clock time
     */
    override fun draw(mousePos: Vec2d, partialTicks: Float) {
        val mousePos = transformFromParentContext(mousePos)
        components.sortBy { it.zIndex }
        if (!isVisible) return

        components.removeAll { e ->
            var b = e.isInvalid
            if (BUS.fire(GuiComponentEvents.RemoveChildEvent(this, e)).isCanceled())
                b = false
            if (e.BUS.fire(GuiComponentEvents.RemoveFromParentEvent(e, this)).isCanceled())
                b = false
            if (b) {
                e.parent = null
            }
            b
        }

        if (wasMouseOver != this.mouseOver) {
            if (this.mouseOver) {
                BUS.fire(GuiComponentEvents.MouseInEvent(this, mousePos))
            } else {
                BUS.fire(GuiComponentEvents.MouseOutEvent(this, mousePos))
            }
        }
        wasMouseOver = this.mouseOver

        GlStateManager.pushMatrix()
        transform.glApply()

        BUS.fire(GuiComponentEvents.PreDrawEvent(this, mousePos, partialTicks))

        drawComponent(mousePos, partialTicks)

        if (LibrarianLib.DEV_ENVIRONMENT && Minecraft.getMinecraft().renderManager.isDebugBoundingBox) {
            GlStateManager.pushAttrib()
            GlStateManager.color(1f, 1f, 1f)
            if (!mouseOver) GlStateManager.color(1f, 0f, 1f)
            GlStateManager.disableTexture2D()
            val tessellator = Tessellator.getInstance()
            val vb = tessellator.buffer
            vb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION)
            vb.pos(0.0, 0.0, 0.0).endVertex()
            vb.pos(size.x, 0.0, 0.0).endVertex()
            vb.pos(size.x, size.y, 0.0).endVertex()
            vb.pos(0.0, size.y, 0.0).endVertex()
            vb.pos(0.0, 0.0, 0.0).endVertex()
            tessellator.draw()
            GlStateManager.enableTexture2D()
            GlStateManager.popAttrib()
        }

        GlStateManager.pushAttrib()

        BUS.fire(GuiComponentEvents.PreChildrenDrawEvent(this, mousePos, partialTicks))
        forEachChild { it.draw(mousePos, partialTicks) }

        GlStateManager.popAttrib()

        BUS.fire(GuiComponentEvents.PostDrawEvent(this, mousePos, partialTicks))

        GlStateManager.popMatrix()
    }

    /**
     * Draw late stuff this component, like tooltips. This method is executed in the root context
     *
     * @param mousePos Mouse position in the root context
     * @param partialTicks From 0-1 the additional fractional ticks, used for smooth animations that aren't dependant on wall-clock time
     */
    fun drawLate(mousePos: Vec2d, partialTicks: Float) {
        if (mouseOver) {
            val tt = tooltip(this)
            if (tt?.isNotEmpty() ?: false) {
                GuiUtils.drawHoveringText(tt, mousePos.xi, mousePos.yi, root.size.xi, root.size.yi, -1,
                        tooltipFont ?: Minecraft.getMinecraft().fontRenderer)
            }
        }

        forEachChild { it.drawLate(mousePos, partialTicks) }
    }

    open fun onTick() {}

    fun tick() {
        BUS.fire(GuiComponentEvents.ComponentTickEvent(this))
        onTick()
        forEachChild(GuiComponent::tick)
    }

    /**
     * Called when the mouse is pressed.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was pressed
     */
    open fun mouseDown(mousePos: Vec2d, button: EnumMouseButton) {
        val mousePos = transformFromParentContext(mousePos)
        if (!isVisible) return
        if (BUS.fire(GuiComponentEvents.MouseDownEvent(this, mousePos, button)).isCanceled())
            return

        if (mouseOver)
            mouseButtonsDown[button.ordinal] = true

        forEachChild { child ->
            child.mouseDown(mousePos, button)
        }
    }

    /**
     * Called when the mouse is released.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was released
     */
    fun mouseUp(mousePos: Vec2d, button: EnumMouseButton) {
        val mousePos = transformFromParentContext(mousePos)
        if (!isVisible) return
        val wasDown = mouseButtonsDown[button.ordinal]
        mouseButtonsDown[button.ordinal] = false

        if (BUS.fire(GuiComponentEvents.MouseUpEvent(this, mousePos, button)).isCanceled())
            return

        if (mouseOver && wasDown) {
            BUS.fire(GuiComponentEvents.MouseClickEvent(this, mousePos, button))
            // don't return here, if a click was handled we should still handle the mouseUp
        }

        forEachChild { child ->
            child.mouseUp(mousePos, button)
        }
    }

    /**
     * Called when the mouse is moved while pressed.
     *
     * @param mousePos The mouse position in the parent context
     * @param button The button that was held
     */
    fun mouseDrag(mousePos: Vec2d, button: EnumMouseButton) {
        val mousePos = transformFromParentContext(mousePos)
        if (!isVisible) return
        if (BUS.fire(GuiComponentEvents.MouseDragEvent(this, mousePos, button)).isCanceled())
            return

        forEachChild { child ->
            child.mouseDrag(mousePos, button)
        }
    }

    /**
     * Called when the mouse wheel is moved.
     *
     * @param mousePos The mouse position in the parent context
     * @param direction The direction the wheel was moved
     */
    fun mouseWheel(mousePos: Vec2d, direction: GuiComponentEvents.MouseWheelDirection) {
        val mousePos = transformFromParentContext(mousePos)
        if (!isVisible) return
        if (BUS.fire(GuiComponentEvents.MouseWheelEvent(this, mousePos, direction)).isCanceled())
            return

        forEachChild { child ->
            child.mouseWheel(mousePos, direction)
        }
    }

    /**
     * Called when a key is pressed in the parent component.
     *
     * @param key The actual character that was pressed
     * @param keyCode The key code, codes listed in [Keyboard]
     */
    fun keyPressed(key: Char, keyCode: Int) {
        if (!isVisible) return
        if (BUS.fire(GuiComponentEvents.KeyDownEvent(this, key, keyCode)).isCanceled())
            return

        keysDown.put(Key[key, keyCode], true)

        forEachChild { child ->
            child.keyPressed(key, keyCode)
        }
    }

    /**
     * Called when a key is released in the parent component.
     *
     * @param key The actual key that was pressed
     * @param keyCode The key code, codes listed in [Keyboard]
     */
    fun keyReleased(key: Char, keyCode: Int) {
        if (!isVisible) return
        keysDown.put(Key[key, keyCode], false) // do this before so we don't have lingering keyDown entries

        if (BUS.fire(GuiComponentEvents.KeyUpEvent(this, key, keyCode)).isCanceled())
            return

        forEachChild { child ->
            child.keyReleased(key, keyCode)
        }
    }

    /**
     * Gets the root component
     */
    val root: GuiComponent
        get() {
            return parent?.root ?: this
        }

    /**
     * Sets the tooltip to be drawn, overriding the existing value. Pass null for the font to use the default font renderer.
     */
    fun setTooltip(text: List<String>, font: FontRenderer?) {
        tooltip(text)
        tooltipFont = font
    }

    /**
     * Sets the tooltip to be drawn, overriding the existing value and using the default font renderer.
     */
    fun setTooltip(text: List<String>) = setTooltip(text, null)

    //=============================================================================
    init {/* Assorted info */
    }
    //=============================================================================

    open fun onMessage(from: GuiComponent, message: GuiComponentEvents.Message) {}

    fun handleMessage(from: GuiComponent, message: GuiComponentEvents.Message) {
        BUS.fire(GuiComponentEvents.MessageArriveEvent(this, from, message))
        onMessage(from, message)

        if (message.rippleType != GuiComponentEvents.EnumRippleType.NONE) {
            if (message.rippleType == GuiComponentEvents.EnumRippleType.UP || message.rippleType == GuiComponentEvents.EnumRippleType.ALL) {
                parent?.let {
                    if (it != from) {
                        it.handleMessage(this, message)
                    }
                }
            }
            if (message.rippleType == GuiComponentEvents.EnumRippleType.DOWN || message.rippleType == GuiComponentEvents.EnumRippleType.ALL) {
                forEachChild {
                    if (it != from) {
                        it.handleMessage(this, message)
                    }
                }
            }
        }
    }

    open fun sendMessage(data: Any, ripple: GuiComponentEvents.EnumRippleType) {
        // NO-OP
    }

    /**
     * Returns all valid data keys for [clazz]. Not guaranteed to be complete.
     */
    fun <D : Any> getAllDataKeys(clazz: Class<D>): Set<String> {
        if (!data.containsKey(clazz))
            return setOf()
        return BUS.fire(GuiComponentEvents.GetDataKeysEvent(this, clazz, data[clazz]?.keys?.toMutableSet() ?: mutableSetOf())).value
    }

    /**
     * Returns all classes for data that contain at least one value. Not guaranteed to be complete.
     */
    fun getAllDataClasses(): Set<Class<*>> {
        return BUS.fire(GuiComponentEvents.GetDataClassesEvent(this, data.entries.filter { it.value.isNotEmpty() }.map { it.key }.toMutableSet())).value
    }

    /**
     * Sets the value associated with the pair of keys [clazz] and [key]. The value must be a subclass of [clazz]
     */
    fun <D : Any> setData(clazz: Class<D>, key: String, value: D) {
        if (!data.containsKey(clazz))
            data.put(clazz, mutableMapOf())
        if (!BUS.fire(GuiComponentEvents.SetDataEvent(this, clazz, key, value)).isCanceled())
            data[clazz]?.put(key, value)
    }

    /**
     * Removes the value associated with the pair of keys [clazz] and [key]
     */
    fun <D : Any> removeData(clazz: Class<D>, key: String) {
        if (!data.containsKey(clazz))
            data.put(clazz, mutableMapOf())
        if (!BUS.fire(GuiComponentEvents.RemoveDataEvent(this, clazz, key, getData(clazz, key))).isCanceled())
            data[clazz]?.remove(key)
    }

    /**
     * Returns the value associated with the pair of keys [clazz] and [key] if it exists, else it returns null.
     * The value will be an instance of [clazz]
     */
    @Suppress("UNCHECKED_CAST")
    fun <D> getData(clazz: Class<D>, key: String): D? {
        if (!data.containsKey(clazz))
            data.put(clazz, HashMap<String, Any>())
        return BUS.fire(GuiComponentEvents.GetDataEvent(this, clazz, key, data[clazz]?.get(key) as D?)).value
    }

    /**
     * Checks if there is a value associated with the pair of keys [clazz] and [key]
     */
    @Suppress("UNCHECKED_CAST")
    fun <D> hasData(clazz: Class<D>, key: String): Boolean {
        if (!data.containsKey(clazz))
            data.put(clazz, HashMap<String, Any>())
        return BUS.fire(GuiComponentEvents.GetDataEvent(this, clazz, key, data[clazz]?.get(key) as D?)).value != null
    }

    /**
     * Sets the value associated with the pair of keys [clazz] and `""`. The value must be a subclass of [clazz]
     */
    fun <D : Any> setData(clazz: Class<D>, value: D) {
        setData(clazz, "", value)
    }

    /**
     * Removes the value associated with the pair of keys [clazz] and `""`
     */
    fun <D : Any> removeData(clazz: Class<D>) {
        removeData(clazz, "")
    }

    /**
     * Returns the value Associated with the pair of keys [clazz] and `""` if it exists, else it returns null.
     * The value will be an instance of [clazz]
     */
    fun <D : Any> getData(clazz: Class<D>): D? {
        return getData(clazz, "")
    }

    /**
     * Checks if there is a value associated with the pair of keys [clazz] and `""`
     */
    fun <D : Any> hasData(clazz: Class<D>): Boolean {
        return hasData(clazz, "")
    }

    /**
     * Adds the passed tag to this component if it doesn't already have it.
     * @return true if the tag didn't exist and was added
     */
    fun addTag(tag: Any): Boolean {
        if (!BUS.fire(GuiComponentEvents.AddTagEvent(this, tag)).isCanceled())
            if (tagStorage.add(tag))
                return true
        return false
    }

    /**
     * Removes the passed tag to this component if it doesn't already have it.
     * @return true if the tag existed and was removed
     */
    fun removeTag(tag: Any): Boolean {
        if (!BUS.fire(GuiComponentEvents.RemoveTagEvent(this, tag)).isCanceled())
            if (tagStorage.remove(tag))
                return true
        return false
    }

    /**
     * Adds or removes the passed tag to this component if it isn't already in the correct state.
     * If [shouldHave] is true this method will add the tag if it doesn't exist, if it is false
     * this method will remove the tag if it does exist
     * @param tag The tag to add or remove
     * @param shouldHave The target state for [hasTag] after calling this method
     * @return True if the tag was added or removed
     */
    fun setTag(tag: Any, shouldHave: Boolean): Boolean {
        if(shouldHave)
            return addTag(tag)
        else
            return removeTag(tag)
    }

    /**
     * Checks if the component has the tag specified. Tags are not case sensitive
     */
    fun hasTag(tag: Any): Boolean {
        return BUS.fire(GuiComponentEvents.HasTagEvent(this, tag, tagStorage.contains(tag))).hasTag
    }

    /**
     * Set this component invalid so it will be removed from it's parent element
     */
    fun invalidate() {
        this.isInvalid = true
    }
}
