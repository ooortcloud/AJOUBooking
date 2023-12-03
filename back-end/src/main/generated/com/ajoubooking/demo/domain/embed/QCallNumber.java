package com.ajoubooking.demo.domain.embed;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCallNumber is a Querydsl query type for CallNumber
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QCallNumber extends BeanPath<CallNumber> {

    private static final long serialVersionUID = -1911536116L;

    public static final QCallNumber callNumber = new QCallNumber("callNumber");

    public final StringPath authorSymbol = createString("authorSymbol");

    public final NumberPath<java.math.BigDecimal> classificationNumber = createNumber("classificationNumber", java.math.BigDecimal.class);

    public QCallNumber(String variable) {
        super(CallNumber.class, forVariable(variable));
    }

    public QCallNumber(Path<? extends CallNumber> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCallNumber(PathMetadata metadata) {
        super(CallNumber.class, metadata);
    }

}

