package com.vicmatskiv.weaponlib.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.vicmatskiv.weaponlib.ClassInfo;
import com.vicmatskiv.weaponlib.compatibility.CompatibleClassInfoProvider;

import net.minecraft.item.ItemBlock;
import net.minecraft.launchwrapper.IClassTransformer;

public class WeaponlibClassTransformer implements IClassTransformer {

    private static ClassInfo entityRendererClassInfo = CompatibleClassInfoProvider.getInstance()
            .getClassInfo("net/minecraft/client/renderer/EntityRenderer");
    
    private static ClassInfo renderBipedClassInfo = CompatibleClassInfoProvider.getInstance()
            .getClassInfo("net/minecraft/client/renderer/entity/RenderBiped");
    
    private static class SetupViewBobbingMethodVisitor extends MethodVisitor {

        public SetupViewBobbingMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM4, mv);
        }
        
        @Override
        public void visitJumpInsn(int opcode, Label label) {
            super.visitJumpInsn(opcode, label);
            if(opcode == Opcodes.IFEQ) {
                mv.visitVarInsn(Opcodes.FLOAD, 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/vicmatskiv/weaponlib/Interceptors", "setupViewBobbing", "(F)Z", false);
                mv.visitJumpInsn(Opcodes.IFEQ, label);
            }
        }
    }
    
    private static class HurtCameraEffectMethodVisitor extends MethodVisitor {
        
        private boolean visited;

        public HurtCameraEffectMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM4, mv);
        }
        
        @Override
        public void visitJumpInsn(int opcode, Label label) {
            super.visitJumpInsn(opcode, label);
            // There are other if statements, replace only the very first one
            if(!visited && opcode == Opcodes.IFEQ) {
                mv.visitVarInsn(Opcodes.FLOAD, 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/vicmatskiv/weaponlib/Interceptors", "hurtCameraEffect", "(F)Z", false);
                mv.visitJumpInsn(Opcodes.IFEQ, label);
                visited = true;
            }
        }
    }

    private static class SetupCameraTransformMethodVisitor extends MethodVisitor {

        public SetupCameraTransformMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM4, mv);
        }

        @SuppressWarnings({ "deprecation"})
        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (entityRendererClassInfo.methodMatches("hurtCameraEffect", "(F)V", owner, name, desc)) {
                super.visitMethodInsn(opcode, owner, name, desc);
                mv.visitVarInsn(Opcodes.FLOAD, 1);
                mv.visitMethodInsn(184, "com/vicmatskiv/weaponlib/Interceptors",
                        "setupCameraTransformAfterHurtCameraEffect", "(F)V");
                return;
            }
            this.mv.visitMethodInsn(opcode, owner, name, desc);
        }
        
        @Override
        public void visitJumpInsn(int opcode, Label label) {
            super.visitJumpInsn(opcode, label);
            // There are other if statements, replace only the very first one
            if(opcode == Opcodes.IFLE) {
                mv.visitVarInsn(Opcodes.FLOAD, 1);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/vicmatskiv/weaponlib/Interceptors", "nauseaCameraEffect", "(F)Z", false);
                mv.visitJumpInsn(Opcodes.IFEQ, label);
            }
        }
    }

    public byte[] transform(String par1, String className, byte[] bytecode) {
        if (entityRendererClassInfo.classMatches(className) || 
                (renderBipedClassInfo != null && renderBipedClassInfo.classMatches(className))) {
            ClassReader cr = new ClassReader(bytecode);
            ClassWriter cw = new ClassWriter(cr, 1);
            CVTransform cv = new CVTransform(cw);
            cr.accept(cv, 0);
            return cw.toByteArray();
        } else {
            return bytecode;
        }
    }
    
    private static class RenderEquippedItemsMethodVisitor extends MethodVisitor {
        private String itemBlockClassName = ItemBlock.class.getName().replace('.', '/');

        public RenderEquippedItemsMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM4, mv);
        }
                
        @Override
        public void visitTypeInsn(int opcode, String type) {            
            if(opcode == Opcodes.INSTANCEOF && type.equals(itemBlockClassName)) {
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/vicmatskiv/weaponlib/Interceptors", "is3dRenderableItem", "(Lnet/minecraft/item/Item;)Z", false);
            } else {
                super.visitTypeInsn(opcode, type);
            }
        }
    }

    private static class CVTransform extends ClassVisitor {
        String classname;

        public CVTransform(ClassVisitor cv) {
            super(Opcodes.ASM4, cv);
        }

        public void visit(int version, int access, String name, String signature, String superName,
                String[] interfaces) {
            this.classname = name;
            this.cv.visit(version, access, name, signature, superName, interfaces);
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if(entityRendererClassInfo.methodMatches("setupCameraTransform", "(FI)V", classname, name, desc)) {
                return new SetupCameraTransformMethodVisitor(cv.visitMethod(access, name, desc, signature, exceptions));
            } else if(entityRendererClassInfo.methodMatches("setupViewBobbing", "(F)V", classname, name, desc)) {
                return new SetupViewBobbingMethodVisitor(cv.visitMethod(access, name, desc, signature, exceptions));
            } else if(entityRendererClassInfo.methodMatches("hurtCameraEffect", "(F)V", classname, name, desc)) {
                return new HurtCameraEffectMethodVisitor(cv.visitMethod(access, name, desc, signature, exceptions));
            } else if(renderBipedClassInfo != null 
                    && renderBipedClassInfo.methodMatches("renderEquippedItems", "(Lnet/minecraft/entity/EntityLiving;F)V", classname, name, desc)) {
                return new RenderEquippedItemsMethodVisitor(cv.visitMethod(access, name, desc, signature, exceptions));
            }
                    
            return this.cv.visitMethod(access, name, desc, signature, exceptions);
        }
    }
}