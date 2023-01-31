package edu.kcg.web3.finalproject.service

import edu.kcg.web3.finalproject.entity.Type
import edu.kcg.web3.finalproject.mapper.TypeMapper
import edu.kcg.web3.finalproject.mapper.TypeUnsafe
import edu.kcg.web3.finalproject.repository.TypeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TypeService(
        @Autowired private val typeRepository: TypeRepository
) {
    fun fetchAll(): List<Type> {
        return typeRepository.findAll()
    }

    fun fetchTypeByIdOrNull(id: Long?): Type? {
        return typeRepository.findByIdOrNull(id)
    }

    fun sanitizeType(typeUnsafe: TypeUnsafe, newType: Type): Type {
        return newType.also { type ->
            type.typeName = typeUnsafe.typeName ?: type.typeName
        }
    }

    fun insertTypeUnsafeOrNull(typeUnsafe: TypeUnsafe): Type? {
        typeUnsafe.typeName?.let {
            typeRepository.findByTypeNameOrNull(it)?.let {
                return null
            } ?: return insertType(sanitizeType(typeUnsafe, Type()))
        } ?: return null
    }

    fun insertType(type: Type): Type {
        return typeRepository.save(type)
    }


    fun updateType(type: Type): Type {
        return typeRepository.save(type)
    }

    fun deleteType(type: Type): TypeMapper {
        val returnType = TypeMapper(type)
        type.placeList.forEach {
            it.typeList.remove(type)
        }
        typeRepository.delete(type)
        return returnType
    }
}