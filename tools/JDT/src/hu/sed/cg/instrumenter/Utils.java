package hu.sed.cg.instrumenter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;

public class Utils {

  private static final List<Modifier> getModifiers(List<IExtendedModifier> modifiers) {
    List<Modifier> result = new ArrayList<Modifier>();

    for (IExtendedModifier modifier : modifiers) {
      if (modifier.isModifier()) {
        result.add((Modifier) modifier);
      }
    }

    return result;
  }

  private static final List<Annotation> getAnnotations(List<IExtendedModifier> modifiers) {
    List<Annotation> result = new ArrayList<Annotation>();

    for (IExtendedModifier modifier : modifiers) {
      if (modifier.isAnnotation()) {
        result.add((Annotation) modifier);
      }
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  private static final boolean isPublicVoidAndHasNoParams(MethodDeclaration method) {
    boolean isPublic = false;
    boolean isVoid = false;
    boolean hasNoParams = false;

    List<Modifier> modifiers = Utils.getModifiers(method.modifiers());
    isPublic = modifiers.size() == 1 && modifiers.get(0).isPublic();

    Type returnType = method.getReturnType2();

    if (returnType != null && returnType.isPrimitiveType()) {
      Code returnTypeCode = ((PrimitiveType) returnType).getPrimitiveTypeCode();

      isVoid = PrimitiveType.VOID.equals(returnTypeCode);
    }

    hasNoParams = method.parameters().isEmpty();

    return isPublic && isVoid && hasNoParams;
  }
  





}
