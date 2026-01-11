//package com.example.theprogress.blocks.machines.furnace
//
//import com.example.theprogress.TheProgress.Companion.MODID
//import com.example.theprogress.mana.IMana
//import com.example.theprogress.mana.IManaReceiver
//import com.example.theprogress.mana.IManaTaker
//import com.example.theprogress.mana.Mana
//import com.example.theprogress.mana.ManaReceiver
//import com.example.theprogress.mana.ManaTaker
//import net.minecraft.server.MinecraftServer
//import net.minecraft.world.level.block.entity.BlockEntityType
//import java.util.stream.Collectors
//import kotlin.math.ceil
//
//abstract class AbstractFurnaceBlockEntity(tileEntityTypeIn: BlockEntityType<*>, private val rmMekanism: RMMekanism) :
//    TileEntity(tileEntityTypeIn), ITickableTileEntity, INamedContainerProvider, IManaReceiver, IManaTaker,
//    ISidedInventory {
//    var customName: ITextComponent? = null
//    var currentSmeltTime = 0
//    private val mana: IMana = Mana(rmMekanism.tier * 100, rmMekanism.tier * 1000)
//    private val manaReceiver: IManaReceiver = ManaReceiver(mana)
//    private val manaTaker: ManaTaker = ManaTaker()
//    val maxSmeltTime = 100 / rmMekanism.tier
//    abstract val upSlots: IntArray
//    abstract val downSlots: IntArray
//    abstract val horizontalSlots: IntArray
//    val inventory: RMItemHandler = RMItemHandler(2 + rmMekanism.supportSlots)
//
//    val name: ITextComponent
//        get() = customName ?: defaultName
//
//    override val rate: Float
//        get() = manaTaker.rate
//
//    private val defaultName: ITextComponent
//        get() = TranslationTextComponent("container.$MODID.${rmMekanism.name}")
//
//    override val isConnectedToManaSpreader: Boolean
//        get() = manaTaker.isConnectedToManaSpreader
//
//    override fun disconnectToManaSpreader() {
//        manaTaker.disconnectToManaSpreader()
//    }
//
//    override val spreaderWorldPos: String
//        get() = manaTaker.spreaderWorldPos
//
//    //IManaReceiver implementation
//    override val currentMana: Int
//        get() = manaReceiver.currentMana
//    override val baseMaxMana: Int
//        get() = manaReceiver.baseMaxMana
//    override val maxTransfer: Int
//        get() = manaReceiver.maxTransfer
//
//
//    override fun getDisplayName(): ITextComponent {
//        return name
//    }
//
//    private fun update() {
//        markDirty()
//        if (world != null && world?.isRemote != true) {
//            world!!.notifyBlockUpdate(getPos(), this.blockState, this.blockState,
//                Constants.BlockFlags.BLOCK_UPDATE)
//        }
//    }
//
//    private fun dropProgress() {
//        currentSmeltTime = 0
//        world!!.setBlockState(getPos(), this.blockState.with(AbstractRMFurnace.LIT, false))
//        update()
//    }
//
//    private fun canBurn(recipe: FurnaceRecipe): Boolean {
//        return mana.currentMana >= 10 &&
//                (inventory.getStackInSlot(1).count == 0
//                        || (inventory.getStackInSlot(1).item == recipe.recipeOutput.item
//                        && inventory.getStackInSlot(1).count + recipe.recipeOutput.count <= inventory.getStackInSlot(1).maxStackSize))
//    }
//
//    override fun tick() {
//        if (world?.isRemote == false) {
//            if (mana.baseMaxMana != mana.currentMana) {
//                val diffMana = mana.baseMaxMana - mana.currentMana
//                val manaToRequest = diffMana / manaTaker.rate
//                if (diffMana > 0){
//                    this.mana.fill(manaTaker.getMana(ceil(manaToRequest).toInt(), world!!.server!!))
//                }
//                else{
//                    val points = Integer.max(((currentMana - mana.baseMaxMana) * (3F/20F)).toInt(), 1)
//                    this.mana.consume(points)
//                }
//            }
//            val recipe = getRecipe(inventory.getStackInSlot(0)) ?: return dropProgress()
//            if (canBurn(recipe)) {
//                world!!.setBlockState(getPos(), this.blockState.with(AbstractRMFurnace.LIT, true))
//                if (currentSmeltTime < maxSmeltTime) {
//                    currentSmeltTime++
//                    update()
//                } else {
//                    if (mana.consume(10)) {
//                        currentSmeltTime = 0
//                        val output = recipe.recipeOutput
//                        inventory.insertItem(1, output.copy(), false)
//                        inventory.decrStackSize(0, 1)
//                        update()
//                    } else {
//                        dropProgress()
//                    }
//                }
//            } else {
//                dropProgress()
//            }
//        }
//    }
//
//
//    override fun read(compound: CompoundNBT) {
//        super.read(compound)
//        if (compound.contains("CustomName", Constants.NBT.TAG_STRING)) {
//            customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"))
//        }
//        mana.loadFromByteArray(compound.getByteArray("Mana"))
//        manaTaker.loadFromByteArray(compound.getByteArray("ManaTaker"))
//        val inv = NonNullList.withSize(inventory.slots, ItemStack.EMPTY)
//        ItemStackHelper.loadAllItems(compound, inv)
//        inventory.setNonNullList(inv)
//        currentSmeltTime = compound.getInt("CurrentSmeltTime")
//    }
//
//    override fun write(compound: CompoundNBT): CompoundNBT {
//        super.write(compound)
//        compound.putByteArray("Mana", mana.toByteArray())
//        compound.putByteArray("ManaTaker", manaTaker.toByteArray())
//        if (customName != null) {
//            compound.putString("CustomName", ITextComponent.Serializer.toJson(customName!!))
//        }
//        ItemStackHelper.saveAllItems(compound, inventory.toNonNullList())
//        compound.putInt("CurrentSmeltTime", currentSmeltTime)
//        return compound
//    }
//
//    private fun getRecipe(stack: ItemStack?): FurnaceRecipe? {
//        if (stack == null) {
//            return null
//        }
//        for(recipe_shape: String in rmMekanism.recipe_shape)
//        {
//            val recipes = findRecipesByType(recipe_shape, world)
//            for (iRecipe in recipes) {
//                val recipe = iRecipe as FurnaceRecipe
//                if (recipe.matches(RecipeWrapper(inventory), this.world!!)) {
//                    return recipe
//                }
//            }
//        }
//        return null
//    }
//
//    override fun getUpdatePacket(): SUpdateTileEntityPacket? {
//        val nbt = CompoundNBT()
//        write(nbt)
//        return SUpdateTileEntityPacket(pos, 0, nbt)
//    }
//
//    override fun onDataPacket(net: NetworkManager, pkt: SUpdateTileEntityPacket) {
//        read(pkt.nbtCompound)
//    }
//
//    override fun getUpdateTag(): CompoundNBT {
//        val nbt = CompoundNBT()
//        write(nbt)
//        return nbt
//    }
//
//    override fun handleUpdateTag(nbt: CompoundNBT) {
//        read(nbt)
//    }
//
//    companion object {
//        fun findRecipesByType(typeIn: String, world: World?): Set<IRecipe<*>> {
//            return if (world != null) world.recipeManager.recipes.stream()
//                .filter { recipe: IRecipe<*> -> recipe.type.toString() == typeIn }
//                .collect(Collectors.toSet()) else emptySet()
//        }
//    }
//
//    override fun getSlotsForFace(side: Direction): IntArray? {
//        return if (side == Direction.DOWN) {
//            downSlots
//        } else {
//            if (side == Direction.UP) upSlots else horizontalSlots
//        }
//    }
//
//    override fun getStackInSlot(index: Int): ItemStack {
//        return this.inventory.get(index)
//    }
//
//    override fun decrStackSize(index: Int, count: Int): ItemStack? {
//        return ItemStackHelper.getAndSplit(this.inventory.getList(), index, count)
//    }
//
//    override fun getSizeInventory(): Int {
//        return inventory.size
//    }
//
//    override fun isEmpty(): Boolean {
//        return this.inventory.isEmpty
//    }
//
//    override fun canInsertItem(index: Int, itemStackIn: ItemStack, direction: Direction?): Boolean {
//        return isItemValidForSlot(index, itemStackIn)
//    }
//
//    override fun isUsableByPlayer(player: PlayerEntity): Boolean {
//        return if (world!!.getTileEntity(pos) !== this) {
//            false
//        } else {
//            player.getDistanceSq(pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5) <= 64.0
//        }
//    }
//
//    override fun canExtractItem(index: Int, stack: ItemStack, direction: Direction): Boolean {
//        if (direction == Direction.DOWN && index == 1) {
//            return true
//        }
//        return false
//    }
//
//    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
//        this.inventory.set(index, stack)
//        if (stack.count > this.inventoryStackLimit) {
//            stack.count = this.inventoryStackLimit
//        }
//    }
//
//    override fun removeStackFromSlot(index: Int): ItemStack {
//        return inventory.getAndRemove(this.inventory.getList(), index)
//    }
//
//    override fun clear() {
//        return this.inventory.clear()
//    }
//
//    override fun isItemValidForSlot(index: Int, stack: ItemStack?): Boolean {
//        return index in upSlots || index in horizontalSlots
//    }
//
//    override fun transfer(points: Int) {
//        manaReceiver.transfer(points)
//        update()
//    }
//
//    override fun connectToManaSpreader(
//        manaSpreader: BlockPos,
//        manaConsumer: BlockPos,
//        server: MinecraftServer,
//        worldId: Int,
//        sensitivity: Float
//    ) {
//        manaTaker.connectToManaSpreader(manaSpreader, manaConsumer, server, worldId, sensitivity)
//    }
//
//}