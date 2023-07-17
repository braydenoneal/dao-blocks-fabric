package com.braydenoneal.dao.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.ArrayList;
import java.util.List;

public class DaoBlock extends HorizontalFacingBlock {
	private final List<List<Integer>> shapes;

	public DaoBlock(Settings settings, List<List<Integer>> shapes) {
		super(settings);
		this.shapes = shapes;
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return this.getModelShape(state);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.getModelShape(state);
	}

	public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.getModelShape(state);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.getModelShape(state);
	}

	public VoxelShape getModelShape(BlockState state) {
		if (state.get(FACING).equals(Direction.SOUTH)) {
			return intsToShape(this.shapes);
		} else if (state.get(FACING).equals(Direction.WEST)) {
			return rotateIntToShapes(this.shapes, 1);
		} else if (state.get(FACING).equals(Direction.NORTH)) {
			return rotateIntToShapes(this.shapes, 2);
		} else {
			return rotateIntToShapes(this.shapes, 3);
		}
	}

	private static VoxelShape intsToShape(List<List<Integer>> shapes) {
		List<VoxelShape> voxelShapeList = new ArrayList<>();

		for (List<Integer> box : shapes) {
			voxelShapeList.add(Block.createCuboidShape(box.get(0), box.get(1), box.get(2), box.get(3), box.get(4), box.get(5)));
		}

		return voxelShapeList.stream().reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
	}

	private static VoxelShape rotateIntToShapes(List<List<Integer>> shapes, int times) {
		if (times > 0) {
			List<List<Integer>> newShapes = new ArrayList<>();

			for (List<Integer> box : shapes) {
				int minX = box.get(0);
				int minY = box.get(1);
				int minZ = box.get(2);
				int maxX = box.get(3);
				int maxY = box.get(4);
				int maxZ = box.get(5);
				newShapes.add(List.of(16 - maxZ, minY, minX, 16 - minZ, maxY, maxX));
			}

			return rotateIntToShapes(newShapes, times - 1);
		} else {
			return intsToShape(shapes);
		}
	}
}
