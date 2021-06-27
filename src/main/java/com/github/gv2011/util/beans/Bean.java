package com.github.gv2011.util.beans;

/**
 * Tagging interface.
 *
 * This interface, annotations or both may be used.
 * Any bean interface annotated as {@link AbstractRoot}, {@link AbstractRoot} or {@link AbstractRoot}
 * may also extend Bean.
 *
 * If an annotations are not present, the inheritance structure is examined.
 *
 * Objects must not directly implement Bean ("obj instanceof Bean" must always tell the truth).
 */
public interface Bean {

}
