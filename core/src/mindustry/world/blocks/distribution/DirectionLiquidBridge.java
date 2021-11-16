package mindustry.world.blocks.distribution;

import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.annotations.Annotations.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.meta.*;

public class DirectionLiquidBridge extends DirectionBridge{
    public final int timerFlow = timers++;

    public float speed = 5f;

    public @Load("@-liquid") TextureRegion liquidRegion;

    public DirectionLiquidBridge(String name){
        super(name);

        outputsLiquid = true;
        group = BlockGroup.liquids;
        canOverdrive = false;
        liquidCapacity = 20f;
        hasLiquids = true;
    }

    public class DuctBridgeBuild extends DirectionBridgeBuild{

        @Override
        public void draw(){
            Draw.rect(block.region, x, y);

            if(liquids.currentAmount() > 0.001f){
                Drawf.liquid(liquidRegion, x, y, liquids.currentAmount() / liquidCapacity, liquids.current().color);
            }

            Draw.rect(dirRegion, x, y, rotdeg());
            var link = findLink();
            if(link != null){
                Draw.z(Layer.power);
                drawBridge(rotation, x, y, link.x, link.y, Tmp.c1.set(liquids.current().color).a(liquids.currentAmount() / liquidCapacity));
            }
        }

        @Override
        public void updateTile(){
            var link = findLink();
            if(link != null){
                moveLiquid(link, liquids.current());
                link.occupied[rotation % 4] = this;
            }

            if(link == null){
                if(liquids.currentAmount() > 0.0001f && timer(timerFlow, 1)){
                    moveLiquidForward(false, liquids.current());
                }
            }

            for(int i = 0; i < 4; i++){
                if(occupied[i] == null || occupied[i].rotation != i || !occupied[i].isValid()){
                    occupied[i] = null;
                }
            }
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            int rel = this.relativeToEdge(source.tile);

            return
                hasLiquids && team == source.team &&
                (liquids.current() == liquid || liquids.get(liquids.current()) < 0.2f) && rel != rotation &&
                (occupied[(rel + 2) % 4] == null || occupied[(rel + 2) % 4] == source);
        }
    }
}
