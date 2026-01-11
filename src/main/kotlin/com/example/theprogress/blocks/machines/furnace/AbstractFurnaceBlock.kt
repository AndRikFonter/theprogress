//package com.example.theprogress.blocks.machines.furnace
//
//import net.minecraft.core.BlockPos
//import net.minecraft.core.Direction
//import net.minecraft.core.particles.ParticleTypes
//import net.minecraft.sounds.SoundEvents
//import net.minecraft.sounds.SoundSource
//import net.minecraft.util.RandomSource
//import net.minecraft.world.InteractionResult
//import net.minecraft.world.MenuProvider
//import net.minecraft.world.entity.LivingEntity
//import net.minecraft.world.entity.player.Player
//import net.minecraft.world.inventory.AbstractContainerMenu
//import net.minecraft.world.item.ItemStack
//import net.minecraft.world.item.context.BlockPlaceContext
//import net.minecraft.world.level.Level
//import net.minecraft.world.level.block.BaseEntityBlock
//import net.minecraft.world.level.block.Block
//import net.minecraft.world.level.block.Mirror
//import net.minecraft.world.level.block.RenderShape
//import net.minecraft.world.level.block.Rotation
//import net.minecraft.world.level.block.entity.BlockEntity
//import net.minecraft.world.level.block.state.BlockState
//import net.minecraft.world.level.block.state.StateDefinition
//import net.minecraft.world.level.block.state.properties.BlockStateProperties
//import net.minecraft.world.level.block.state.properties.BooleanProperty
//import net.minecraft.world.level.block.state.properties.EnumProperty
//import net.minecraft.world.phys.BlockHitResult
//
//abstract class AbstractRMFurnaceBlock(properties: Properties) : BaseEntityBlock(properties) {
//
//    init {
//        // В 1.21.x дефолтное состояние устанавливается через registerDefaultState
//        registerDefaultState(stateDefinition.any()
//            .setValue(FACING, Direction.NORTH)
//            .setValue(LIT, false))
//    }
//
//    // Заменяет hasTileEntity
//    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
//        // Здесь должен быть ваш метод создания TileEntity
//        return null
//    }
//
//    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
//        builder.add(FACING, LIT)
//    }
//
//    // Поворот и зеркальное отображение
//    override fun mirror(state: BlockState, mirror: Mirror): BlockState {
//        return state.rotate(mirror.getRotation(state.getValue(FACING)))
//    }
//
//    override fun rotate(state: BlockState, rot: Rotation): BlockState {
//        return state.setValue(FACING, rot.rotate(state.getValue(FACING)))
//    }
//
//    // В 1.21.11 свет настраивается в свойствах блока (Properties) при регистрации
//    // Но если нужно динамически:
//    override fun getLightEmission(state: BlockState, level: net.minecraft.world.level.BlockGetter, pos: BlockPos): Int {
//        return if (state.getValue(LIT)) 13 else 0
//    }
//
//    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
//        return defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
//    }
//
//    // Компоратор
//    override fun hasAnalogOutputSignal(state: BlockState): Boolean = true
//
//    override fun getAnalogOutputSignal(state: BlockState, level: Level, pos: BlockPos, direction: Direction): Int {
//        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos))
//    }
//
//    // Эффекты (частицы и звуки)
//    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, rand: RandomSource) {
//        if (state.getValue(LIT)) {
//            val d0 = pos.x.toDouble() + 0.5
//            val d1 = pos.y.toDouble()
//            val d2 = pos.z.toDouble() + 0.5
//
//            if (rand.nextDouble() < 0.1) {
//                level.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0f, 1.0f, false)
//            }
//
//            val direction: Direction = state.getValue(FACING)
//            val axis = direction.axis
//            val d4 = rand.nextDouble() * 0.6 - 0.3
//            val d5 = if (axis === Direction.Axis.X) direction.stepX.toDouble() * 0.52 else d4
//            val d6 = rand.nextDouble() * 6.0 / 16.0
//            val d7 = if (axis === Direction.Axis.Z) direction.stepZ.toDouble() * 0.52 else d4
//
//            level.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0)
//            level.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0)
//        }
//    }
//
//    // Клик по блоку (В 1.21.11 метод называется useWithoutItem)
//    override fun useWithoutItem(state: BlockState, level: Level, pos: BlockPos, player: Player, hit: BlockHitResult): InteractionResult {
//        if (level.isClientSide) return InteractionResult.SUCCESS
//
//        val be = level.getBlockEntity(pos)
//        if (be is MenuProvider) {
//            // Реализация логики Shift из вашего кода
//            // Примечание: проверка KeyboardHelper на сервере невозможна,
//            // используйте player.isShiftKeyDown
//            if (player.isShiftKeyDown && player.mainHandItem.isEmpty) {
//                // Ваша логика маны (RMCCMessage)
//            } else {
//                player.openMenu(be)
//            }
//        }
//        return InteractionResult.CONSUME
//    }
//
//    // Выпадение вещей при разрушении
////    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
////        if (!state.`is`(newState.block)) {
////            val be = level.getBlockEntity(pos)
////            if (be is AbstractFurnaceBlockEntity) {
////                // Containers.dropContents — стандартный способ NeoForge 2026
////                Containers.dropContents(level, pos, be.inventory)
////                level.updateNeighborsAt(pos, this)
////            }
////            super.onRemove(state, level, pos, newState, isMoving)
////        }
////    }
//
////    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
////        if (stack.has(DataComponents.CUSTOM_NAME)) {
////            val be = level.getBlockEntity(pos)
////            if (be is AbstractFurnaceBlockEntity) {
////                be.customName = stack.hoverName
////            }
////        }
////    }
//
//    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL
//
//    companion object {
//        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING
//        val LIT: BooleanProperty = BlockStateProperties.LIT
//    }
//}