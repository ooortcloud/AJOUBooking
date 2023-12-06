package com.ajoubooking.demo.domain.embed;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QColumnAddress is a Querydsl query type for ColumnAddress
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QColumnAddress extends BeanPath<ColumnAddress> {

    private static final long serialVersionUID = 587177689L;

    public static final QColumnAddress columnAddress = new QColumnAddress("columnAddress");

    public final NumberPath<Integer> bookshelfNum = createNumber("bookshelfNum", Integer.class);

    public final NumberPath<Integer> category = createNumber("category", Integer.class);

    public final NumberPath<Integer> columnNum = createNumber("columnNum", Integer.class);

    public QColumnAddress(String variable) {
        super(ColumnAddress.class, forVariable(variable));
    }

    public QColumnAddress(Path<? extends ColumnAddress> path) {
        super(path.getType(), path.getMetadata());
    }

    public QColumnAddress(PathMetadata metadata) {
        super(ColumnAddress.class, metadata);
    }

}

