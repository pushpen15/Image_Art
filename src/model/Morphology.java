/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/*-
 * #%L
 * Mathematical morphology library and plugins for ImageJ/Fiji.
 * %%
 * Copyright (C) 2014 - 2017 INRA.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import static java.lang.Math.max;
import static java.lang.Math.min;
import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import inra.ijpb.data.image.ColorImages;
import inra.ijpb.morphology.Strel;
import inra.ijpb.morphology.Strel3D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * Collection of static methods for morphological filters,
 * as well as an enumeration of available methods.
 * </p>
 * 
 * <p>
 * Example of use:
 * <pre><code>
 * ImageProcessor image = IJ.getImage().getProcessor();
 * Strel se = SquareStrel.fromDiameter(5);
 * ImageProcessor grad = Morphology.gradient(image, se);
 * ImagePlus res = new ImagePlus("Gradient", grad);
 * res.show(); 
 * </code></pre>
 * 
 * <p>
 * Example of use with 3D image (stack):
 * <pre><code>
 * ImageStack image = IJ.getImage().getStack();
 * Strel3D se = CubeStrel.fromDiameter(3);
 * ImageStack grad = Morphology.gradient(image, se);
 * ImagePlus res = new ImagePlus("Gradient3D", grad);
 * res.show(); 
 * </code></pre>
 * @author David Legland
 *
 */
public class Morphology 
{
	// =======================================================================
	// Enumeration for operations
	
	/**
	 * A pre-defined set of basis morphological operations, that can be easily 
	 * used with a GenericDialog. 
	 * Example:
	 * <pre><code>
	 * // Use a generic dialog to define an operator 
	 * GenericDialog gd = new GenericDialog();
	 * gd.addChoice("Operation", Operation.getAllLabels();
	 * gd.showDialog();
	 * Operation op = Operation.fromLabel(gd.getNextChoice());
	 * // Apply the operation on the current image
	 * ImageProcessor image = IJ.getImage().getProcessor();
	 * op.apply(image, SquareStrel.fromRadius(2));
	 * </code></pre>
	 */
	public enum Operation 
	{
		/** Morphological erosion (local minima)*/
		EROSION("Erosion"),
		/** Morphological dilation (local maxima)*/
		DILATION("Dilation"),
		/** Morphological opening (erosion followed by dilation)*/
		OPENING("Opening"),
		/** Morphological closing (dilation followed by erosion)*/
		CLOSING("Closing"), 
		/** White Top-Hat */
		TOPHAT("White Top Hat"),
		/** Black Top-Hat */
		BOTTOMHAT("Black Top Hat"),
		/** Morphological gradient (difference of dilation with erosion) */
		GRADIENT("Gradient"), 
		/** Morphological laplacian (difference of external gradient with internal gradient) */
		LAPLACIAN("Laplacian"), 
		/** Morphological internal gradient (difference of dilation with original image) */
		INTERNAL_GRADIENT("Internal Gradient"), 
		/** Morphological internal gradient (difference of original image with erosion) */
		EXTERNAL_GRADIENT("External Gradient");
		
		private final String label;
		
		private Operation(String label) 
		{
			this.label = label;
		}
		
		/**
		 * Applies the current operator to the input image.
		 * 
		 * @param image
		 *            the image to process
		 * @param strel
		 *            the structuring element to use
		 * @return the result of morphological operation applied to image
		 */
		public ImageProcessor apply(ImageProcessor image, Strel strel) 
		{
			if (this == DILATION)
				return dilation(image, strel);
			if (this == EROSION)
				return erosion(image, strel);
			if (this == CLOSING)
				return closing(image, strel);
			if (this == OPENING)
				return opening(image, strel);
			if (this == TOPHAT)
				return whiteTopHat(image, strel);
			if (this == BOTTOMHAT)
				return blackTopHat(image, strel);
			if (this == GRADIENT)
				return gradient(image, strel);
			if (this == LAPLACIAN)
				return laplacian(image, strel);
			if (this == INTERNAL_GRADIENT)
				return internalGradient(image, strel);
			if (this == EXTERNAL_GRADIENT)
				return externalGradient(image, strel);
			
			throw new RuntimeException(
					"Unable to process the " + this + " morphological operation");
		}
		
		/**
		 * Applies the current operator to the input 3D image.
		 * 
		 * @param image
		 *            the image to process
		 * @param strel
		 *            the structuring element to use
		 * @return the result of morphological operation applied to image
		 */
		public ImageStack apply(ImageStack image, Strel3D strel)
		{
			if (this == DILATION)
				return dilation(image, strel);
			if (this == EROSION)
				return erosion(image, strel);
			if (this == CLOSING)
				return closing(image, strel);
			if (this == OPENING)
				return opening(image, strel);
			if (this == TOPHAT)
				return whiteTopHat(image, strel);
			if (this == BOTTOMHAT)
				return blackTopHat(image, strel);
			if (this == GRADIENT)
				return gradient(image, strel);
			if (this == LAPLACIAN)
				return laplacian(image, strel);
			if (this == INTERNAL_GRADIENT)
				return internalGradient(image, strel);
			if (this == EXTERNAL_GRADIENT)
				return externalGradient(image, strel);
			
			throw new RuntimeException(
					"Unable to process the " + this + " morphological operation");
		}
		
		public String toString() 
		{
			return this.label;
		}
		
		public static String[] getAllLabels()
		{
			int n = Operation.values().length;
			String[] result = new String[n];
			
			int i = 0;
			for (Operation op : Operation.values())
				result[i++] = op.label;
			
			return result;
		}
		
		/**
		 * Determines the operation type from its label.
		 * 
		 * @param opLabel
		 *            the label of the operation
		 * @return the parsed Operation
		 * @throws IllegalArgumentException
		 *             if label is not recognized.
		 */
		public static Operation fromLabel(String opLabel)
		{
			if (opLabel != null)
				opLabel = opLabel.toLowerCase();
			for (Operation op : Operation.values()) 
			{
				String cmp = op.label.toLowerCase();
				if (cmp.equals(opLabel))
					return op;
			}
			throw new IllegalArgumentException("Unable to parse Operation with label: " + opLabel);
		}
	};
	
	/**
	 * Makes the default constructor private to avoid creation of instances.
	 */
	private Morphology() 
	{
	}

	
	// =======================================================================
	// Main morphological operations
	
	/**
	 * Performs morphological dilation on the input image.
	 * 
	 * Dilation is obtained by extracting the maximum value among pixels in the
	 * neighborhood given by the structuring element.
	 * 
	 * This methods is mainly a wrapper to the dilation method of the strel
	 * object.
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for dilation
	 * @return the result of the dilation
	 * 
	 * @see #erosion(ImageProcessor, Strel)
	 * @see Strel#dilation(ImageProcessor)
	 */
	public static ImageProcessor dilation(ImageProcessor image, Strel strel)
	{
		checkImageType(image);
		if (image instanceof ColorProcessor)
			return dilationRGB(image, strel);
		
		return strel.dilation(image);
	}

	/**
	 * Performs morphological dilation on each channel, and reconstitutes the
	 * resulting color image.
	 * 
	 * @param image
	 *            the input RGB image
	 * @param strel
	 *            the structuring element used for dilation
	 * @return the result of the dilation
	 */
	private static ImageProcessor dilationRGB(ImageProcessor image, Strel strel) 
	{
		// extract channels and allocate memory for result
		Map<String, ByteProcessor> channels = ColorImages.mapChannels(image);
		Collection<ImageProcessor> res = new ArrayList<ImageProcessor>(channels.size());
		
		// Process each channel individually
		for (String name : new String[]{"red", "green", "blue"}) 
		{
			strel.setChannelName(name);
			res.add(strel.dilation(channels.get(name)));
		}
		
		return ColorImages.mergeChannels(res);
	}

	/**
	 * Performs morphological dilation on the input 3D image.
	 * 
	 * Dilation is obtained by extracting the maximum value among voxels in the
	 * neighborhood given by the 3D structuring element.
	 * 
	 * @param image
	 *            the input 3D image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for dilation
	 * @return the result of the dilation
	 */
	public static ImageStack dilation(ImageStack image, Strel3D strel)
	{
		checkImageType(image);
		return strel.dilation(image);
	}
	
	/**
	 * Performs morphological erosion on the input image. Erosion is obtained by
	 * extracting the minimum value among pixels in the neighborhood given by
	 * the structuring element.
	 * 
	 * This methods is mainly a wrapper to the erosion method of the strel
	 * object.
	 * 
	 * @see #dilation(ImageProcessor, Strel)
	 * @see Strel#erosion(ImageProcessor)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for erosion
	 * @return the result of the erosion
	 */
	public static ImageProcessor erosion(ImageProcessor image, Strel strel)
	{
		checkImageType(image);
		if (image instanceof ColorProcessor)
			return erosionRGB(image, strel);

		return strel.erosion(image);
	}

	/**
	 * Performs morphological erosion on each channel, and reconstitutes the
	 * resulting color image.
	 * 
	 * @param image
	 *            the input image to process (RGB)
	 * @param strel
	 *            the structuring element used for erosion
	 * @return the result of the erosion
	 */
	private static ImageProcessor erosionRGB(ImageProcessor image, Strel strel)
	{
		// extract channels and allocate memory for result
		Map<String, ByteProcessor> channels = ColorImages.mapChannels(image);
		Collection<ImageProcessor> res = new ArrayList<ImageProcessor>(channels.size());
		
		// Process each channel individually
		for (String name : new String[]{"red", "green", "blue"}) 
		{
			strel.setChannelName(name);
			res.add(strel.erosion(channels.get(name)));
		}
		
		return ColorImages.mergeChannels(res);
	}
	
	/**
	 * Performs morphological erosion on the input 3D image.
	 * 
	 * Erosion is obtained by extracting the minimum value among voxels in the
	 * neighborhood given by the 3D structuring element.
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for erosion
	 * @return the result of the erosion
	 */
	public static ImageStack erosion(ImageStack image, Strel3D strel) 
	{
		checkImageType(image);
		return strel.erosion(image);
	}

	/**
	 * Performs morphological opening on the input image.
	 * 
	 * The opening is obtained by performing an erosion followed by an dilation
	 * with the reversed structuring element.
	 * 
	 * This methods is mainly a wrapper to the opening method of the strel object.
	 * 
	 * @see #closing(ImageProcessor, Strel)
	 * @see Strel#opening(ImageProcessor)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for opening
	 * @return the result of the morphological opening
	 */
	public static ImageProcessor opening(ImageProcessor image, Strel strel)
	{
		checkImageType(image);
		if (image instanceof ColorProcessor)
			return openingRGB(image, strel);

		return strel.opening(image);
	}

	/**
	 * Performs morphological opening on each channel, and reconstitutes the
	 * resulting color image.
	 */
	private static ImageProcessor openingRGB(ImageProcessor image, Strel strel)
	{
		// extract channels and allocate memory for result
		Map<String, ByteProcessor> channels = ColorImages.mapChannels(image);
		Collection<ImageProcessor> res = new ArrayList<ImageProcessor>(channels.size());
		
		// Process each channel individually
		for (String name : new String[]{"red", "green", "blue"}) 
		{
			strel.setChannelName(name);
			res.add(strel.opening(channels.get(name)));
		}
		
		return ColorImages.mergeChannels(res);
	}
	
	/**
	 * Performs morphological opening on the input 3D image.
	 * 
	 * The 3D opening is obtained by performing a 3D erosion followed by a 3D
	 * dilation with the reversed structuring element.
	 * 
	 * @see #closing(ImageStack, Strel3D)
	 * @see Strel#opening(ImageStack)
	 * 
	 * @param image
	 *            the input 3D image to process
	 * @param strel
	 *            the structuring element used for opening
	 * @return the result of the 3D morphological opening
	 */
	public static ImageStack opening(ImageStack image, Strel3D strel) 
	{
		checkImageType(image);
		return strel.opening(image);
	}


	/**
	 * Performs closing on the input image.
	 * The closing is obtained by performing a dilation followed by an erosion
	 * with the reversed structuring element.
	 *  
	 * This methods is mainly a wrapper to the opening method of the strel object.
	 * @see #opening(ImageProcessor, Strel)
	 * @see Strel#closing(ImageProcessor)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for closing
	 * @return the result of the morphological closing
	 */
	public static ImageProcessor closing(ImageProcessor image, Strel strel) 
	{
		checkImageType(image);
		if (image instanceof ColorProcessor)
			return closingRGB(image, strel);

		return strel.closing(image);
	}

	/**
	 * Performs morphological closing on each channel, and reconstitutes the
	 * resulting color image.
	 */
	private static ImageProcessor closingRGB(ImageProcessor image, Strel strel)
	{
		// extract channels and allocate memory for result
		Map<String, ByteProcessor> channels = ColorImages.mapChannels(image);
		Collection<ImageProcessor> res = new ArrayList<ImageProcessor>(channels.size());
		
		// Process each channel individually
		for (String name : new String[]{"red", "green", "blue"})
		{
			strel.setChannelName(name);
			res.add(strel.closing(channels.get(name)));
		}
		
		return ColorImages.mergeChannels(res);
	}
	
	/**
	 * Performs morphological closing on the input 3D image.
	 * 
	 * The 3D closing is obtained by performing a 3D dilation followed by a 3D
	 * erosion with the reversed structuring element.
	 * 
	 * @see #opening(ImageStack, Strel3D)
	 * @see Strel#opening(ImageStack)
	 * 
	 * @param image
	 *            the input 3D image to process
	 * @param strel
	 *            the structuring element used for closing
	 * @return the result of the 3D morphological closing
	 */
	public static ImageStack closing(ImageStack image, Strel3D strel) 
	{
		checkImageType(image);
		return strel.closing(image);
	}


	/**
	 * Computes white top hat of the original image.
	 * The white top hat is obtained by subtracting the result of an opening 
	 * from the original image.
	 *  
	 * The white top hat enhances light structures smaller than the structuring element.
	 * 
	 * @see #blackTopHat(ImageProcessor, Strel)
	 * @see #opening(ImageProcessor, Strel)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for white top-hat
	 * @return the result of the white top-hat
	 */
	public static ImageProcessor whiteTopHat(ImageProcessor image, Strel strel) 
	{
		checkImageType(image);
		if (image instanceof ColorProcessor)
			return whiteTopHatRGB(image, strel);

		// First performs closing
		ImageProcessor result = strel.opening(image);
		
		// Compute subtraction of result from original image
		int count = image.getPixelCount();
		if (image instanceof ByteProcessor) 
		{
			for (int i = 0; i < count; i++) 
			{
				// Forces computation using integers, because opening with 
				// octagons can greater than original image (bug)
				int v1 = image.get(i);
				int v2 = result.get(i);
				result.set(i, clamp(v1 - v2, 0, 255));
			}
		} 
		else 
		{
			for (int i = 0; i < count; i++) 
			{
				float v1 = image.getf(i);
				float v2 = result.getf(i);
				result.setf(i, v1 - v2);
			}
		}

		return result;
	}
	
	/**
	 * Performs morphological closing on each channel, and reconstitutes the
	 * resulting color image.
	 */
	private static ImageProcessor whiteTopHatRGB(ImageProcessor image, Strel strel) 
	{
		// extract channels and allocate memory for result
		Map<String, ByteProcessor> channels = ColorImages.mapChannels(image);
		Collection<ImageProcessor> res = new ArrayList<ImageProcessor>(channels.size());
		
		// Process each channel individually
		for (String name : new String[]{"red", "green", "blue"}) 
		{
			strel.setChannelName(name);
			res.add(whiteTopHat(channels.get(name), strel));
		}

		// create new color image
		return ColorImages.mergeChannels(res);
	}
	
	/**
	 * Computes 3D white top hat of the original image.
	 * 
	 * The white top hat is obtained by subtracting the result of an opening 
	 * from the original image.
	 *  
	 * The white top hat enhances light structures smaller than the structuring element.
	 * 
	 * @see #blackTopHat(ImageStack, Strel3D)
	 * @see #opening(ImageStack, Strel3D)
	 * 
	 * @param image
	 *            the input 3D image to process 
	 * @param strel
	 *            the structuring element used for white top-hat
	 * @return the result of the 3D white top-hat
	 */
	public static ImageStack whiteTopHat(ImageStack image, Strel3D strel)
	{
		checkImageType(image);
		
		// First performs opening
		ImageStack result = strel.opening(image);
		
		// compute max possible value
		double maxVal = getMaxPossibleValue(image);
		
		// Compute subtraction of result from original image
		int nx = image.getWidth();
		int ny = image.getHeight();
		int nz = image.getSize();
		for (int z = 0; z < nz; z++)
		{
			for (int y = 0; y < ny; y++)
			{
				for (int x = 0; x < nx; x++) 
				{
					double v1 = image.getVoxel(x, y, z);
					double v2 = result.getVoxel(x, y, z);
					result.setVoxel(x, y, z, min(max(v1 - v2, 0), maxVal));
				}
			}
		}
		
		return result;
	}



	/**
	 * Computes black top hat (or "bottom hat") of the original image.
	 * The black top hat is obtained by subtracting the original image from
	 * the result of a closing.
	 *  
	 * The black top hat enhances dark structures smaller than the structuring element.
	 * 
	 * @see #whiteTopHat(ImageProcessor, Strel)
	 * @see #closing(ImageProcessor, Strel)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for black top-hat
	 * @return the result of the black top-hat
	 */
	public static ImageProcessor blackTopHat(ImageProcessor image, Strel strel)
	{
		checkImageType(image);
		if (image instanceof ColorProcessor)
			return blackTopHatRGB(image, strel);

		// First performs closing
		ImageProcessor result = strel.closing(image);
		
		// Compute subtraction of result from original image
		int count = image.getPixelCount();
		if (image instanceof ByteProcessor) 
		{
			for (int i = 0; i < count; i++)
			{
				// Forces computation using integers, because closing with 
				// octagons can lower than than original image (bug)
				int v1 = result.get(i);
				int v2 = image.get(i);
				result.set(i, clamp(v1 - v2, 0, 255));
			}
		} 
		else 
		{
			for (int i = 0; i < count; i++)
			{
				float v1 = result.getf(i);
				float v2 = image.getf(i);
				result.setf(i, v1 - v2);
			}
		}
		return result;
	}
	
	/**
	 * Performs morphological black top hat on each channel, and reconstitutes
	 * the resulting color image.
	 */
	private static ImageProcessor blackTopHatRGB(ImageProcessor image, Strel strel)
	{
		// extract channels and allocate memory for result
		Map<String, ByteProcessor> channels = ColorImages.mapChannels(image);
		Collection<ImageProcessor> res = new ArrayList<ImageProcessor>(channels.size());
		
		// Process each channel individually
		for (String name : new String[]{"red", "green", "blue"})
		{
			strel.setChannelName(name);
			res.add(blackTopHat(channels.get(name), strel));
		}
		
		return ColorImages.mergeChannels(res);
	}
	
	/**
	 * Computes black top hat (or "bottom hat") of the original image.
	 * The black top hat is obtained by subtracting the original image from
	 * the result of a closing.
	 *  
	 * The black top hat enhances dark structures smaller than the structuring element.
	 * 
	 * @see #whiteTopHat(ImageStack, Strel3D)
	 * @see #closing(ImageStack, Strel3D)
	 * 
	 * @param image
	 *            the input 3D image to process
	 * @param strel
	 *            the structuring element used for black top-hat
	 * @return the result of the 3D black top-hat
	 */
	public static ImageStack blackTopHat(ImageStack image, Strel3D strel)
	{
		checkImageType(image);
		
		// First performs closing
		ImageStack result = strel.closing(image);
		
		// Compute subtraction of result from original image
		int nx = image.getWidth();
		int ny = image.getHeight();
		int nz = image.getSize();
		for (int z = 0; z < nz; z++) {
			for (int y = 0; y < ny; y++) {
				for (int x = 0; x < nx; x++) {
					double v1 = result.getVoxel(x, y, z);
					double v2 = image.getVoxel(x, y, z);
					result.setVoxel(x, y, z, min(max(v1 - v2, 0), 255));
				}
			}
		}
		
		return result;
	}

	
	/**
	 * Computes the morphological gradient of the input image.
	 * The morphological gradient is obtained by from the difference of image 
	 * dilation and image erosion computed with the same structuring element. 
	 * 
	 * @see #erosion(ImageProcessor, Strel)
	 * @see #dilation(ImageProcessor, Strel)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for morphological gradient
	 * @return the result of the morphological gradient
	 */
	public static ImageProcessor gradient(ImageProcessor image, Strel strel)
	{
		checkImageType(image);
		if (image instanceof ColorProcessor)
			return gradientRGB(image, strel);

		// First performs dilation and erosion
		ImageProcessor result = strel.dilation(image);
		ImageProcessor eroded = strel.erosion(image);

		// Subtract erosion from dilation
		int count = image.getPixelCount();
		if (image instanceof ByteProcessor)
		{
			for (int i = 0; i < count; i++) 
			{
				// Forces computation using integers, because opening with 
				// octagons can greater than original image (bug)
				int v1 = result.get(i);
				int v2 = eroded.get(i);
				result.set(i, clamp(v1 - v2, 0, 255));
			}
		} 
		else 
		{
			for (int i = 0; i < count; i++)
			{
				float v1 = result.getf(i);
				float v2 = eroded.getf(i);
				result.setf(i, v1 - v2);
			}
		}
		// free memory
		eroded = null;
		
		// return gradient
		return result;
	}

	/**
	 * Performs morphological gradient on each channel, and reconstitutes
	 * the resulting color image.
	 */
	private static ImageProcessor gradientRGB(ImageProcessor image, Strel strel)
	{
		// extract channels and allocate memory for result
		Map<String, ByteProcessor> channels = ColorImages.mapChannels(image);
		Collection<ImageProcessor> res = new ArrayList<ImageProcessor>(channels.size());
		
		// Process each channel individually
		for (String name : new String[]{"red", "green", "blue"})
		{
			strel.setChannelName(name);
			res.add(gradient(channels.get(name), strel));
		}
		
		return ColorImages.mergeChannels(res);
	}

	/**
	 * Computes the morphological gradient of the input 3D image.
	 * The morphological gradient is obtained by from the difference of image 
	 * dilation and image erosion computed with the same structuring element. 
	 * 
	 * @see #erosion(ImageStack, Strel3D)
	 * @see #dilation(ImageStack, Strel3D)
	 * 
	 * @param image
	 *            the input 3D image to process
	 * @param strel
	 *            the structuring element used for morphological gradient
	 * @return the result of the 3D morphological gradient
	 */
	public static ImageStack gradient(ImageStack image, Strel3D strel)
	{
		checkImageType(image);
		
		// First performs dilation and erosion
		ImageStack result = strel.dilation(image);
		ImageStack eroded = strel.erosion(image);
		
		// Determine max possible value from bit depth
		double maxVal = getMaxPossibleValue(image);

		// Compute subtraction of result from original image
		int nx = image.getWidth();
		int ny = image.getHeight();
		int nz = image.getSize();
		for (int z = 0; z < nz; z++) 
		{
			for (int y = 0; y < ny; y++) 
			{
				for (int x = 0; x < nx; x++) 
				{
					double v1 = result.getVoxel(x, y, z);
					double v2 = eroded.getVoxel(x, y, z);
					result.setVoxel(x, y, z, min(max(v1 - v2, 0), maxVal));
				}
			}
		}
		
		return result;
	}


	/**
	 * Computes the morphological Laplacian of the input image. The
	 * morphological gradient is obtained from the difference of the external
	 * gradient with the internal gradient, both computed with the same
	 * structuring element.
	 * 
	 * Homogeneous regions appear as gray.
	 * 
	 * @see #erosion(ImageProcessor, Strel)
	 * @see #dilation(ImageProcessor, Strel)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for morphological laplacian
	 * @return the result of the morphological laplacian
	 */
	public static ImageProcessor laplacian(ImageProcessor image, Strel strel) 
	{
		checkImageType(image);
		if (image instanceof ColorProcessor)
			return laplacianRGB(image, strel);

		// First performs dilation and erosion
		ImageProcessor outer = externalGradient(image, strel);
		ImageProcessor inner = internalGradient(image, strel);
		
		// Subtract inner gradient from outer gradient
		ImageProcessor result = image.duplicate();
		int count = image.getPixelCount();
		if (image instanceof ByteProcessor) 
		{
			for (int i = 0; i < count; i++)
			{
				// Forces computation using integers, because opening with 
				// octagons can be greater than original image (bug)
				int v1 = outer.get(i);
				int v2 = inner.get(i);
				result.set(i, clamp(v1 - v2 + 128, 0, 255));
			}
		}
		else
		{
			for (int i = 0; i < count; i++) 
			{
				float v1 = outer.getf(i);
				float v2 = inner.getf(i);
				result.setf(i, v1 - v2);
			}
		}
		// free memory
		outer = null;
		inner = null;
		
		// return gradient
		return result;
	}

	/**
	 * Performs morphological Laplacian on each channel, and reconstitutes
	 * the resulting color image.
	 * 
	 * Homogeneous regions appear as gray.
	 */
	private static ImageProcessor laplacianRGB(ImageProcessor image, Strel strel) 
	{
		// extract channels and allocate memory for result
		Map<String, ByteProcessor> channels = ColorImages.mapChannels(image);
		Collection<ImageProcessor> res = new ArrayList<ImageProcessor>(channels.size());
		
		// Process each channel individually
		for (String name : new String[]{"red", "green", "blue"}) 
		{
			strel.setChannelName(name);
			res.add(laplacian(channels.get(name), strel));
		}
		
		return ColorImages.mergeChannels(res);
	}

	/**
	 * Computes the morphological Laplacian of the 3D input image. The
	 * morphological gradient is obtained from the difference of the external
	 * gradient with the internal gradient, both computed with the same
	 * structuring element.
	 * 
	 * Homogeneous regions appear as gray.
	 * 
	 * @see #externalGradient(ImageStack, Strel3D)
	 * @see #internalGradient(ImageStack, Strel3D)
	 * 
	 * @param image
	 *            the input 3D image to process 
	 * @param strel
	 *            the structuring element used for morphological laplacian
	 * @return the result of the 3D morphological laplacian
	 */
	public static ImageStack laplacian(ImageStack image, Strel3D strel)
	{
		checkImageType(image);
		
		// First performs dilation and erosion
		ImageStack outer = externalGradient(image, strel);
		ImageStack inner = internalGradient(image, strel);
		
		// Determine max possible value from bit depth
		double maxVal = getMaxPossibleValue(image);
		double midVal = maxVal / 2;
		
		// Compute subtraction of result from original image
		int nx = image.getWidth();
		int ny = image.getHeight();
		int nz = image.getSize();
		for (int z = 0; z < nz; z++) 
		{
			for (int y = 0; y < ny; y++)
			{
				for (int x = 0; x < nx; x++)
				{
					double v1 = outer.getVoxel(x, y, z);
					double v2 = inner.getVoxel(x, y, z);
					outer.setVoxel(x, y, z, min(max(v1 - v2 + midVal, 0), maxVal));
				}
			}
		}
		
		return outer;
	}

	/** 
	 * Computes the morphological internal gradient of the input image.
	 * The morphological internal gradient is obtained by from the difference 
	 * of original image with the result of an erosion.
	 * 
	 * @see #erosion(ImageProcessor, Strel)
	 * @see #gradient(ImageProcessor, Strel)
	 * @see #externalGradient(ImageProcessor, Strel)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for morphological internal gradient
	 * @return the result of the morphological internal gradient
	 */
	public static ImageProcessor internalGradient(ImageProcessor image, Strel strel) 
	{
		checkImageType(image);
		if (image instanceof ColorProcessor)
			return internalGradientRGB(image, strel);

		// First performs erosion
		ImageProcessor result = strel.erosion(image);

		// Subtract erosion result from original image
		int count = image.getPixelCount();
		if (image instanceof ByteProcessor)
		{
			for (int i = 0; i < count; i++)
			{
				// Forces computation using integers, because opening with 
				// octagons can be greater than original image (bug)
				int v1 = image.get(i);
				int v2 = result.get(i);
				result.set(i, clamp(v1 - v2, 0, 255));
			}
		} 
		else 
		{
			for (int i = 0; i < count; i++)
			{
				float v1 = image.getf(i);
				float v2 = result.getf(i);
				result.setf(i, v1 - v2);
			}
		}
		// return gradient
		return result;
	}

	private static ImageProcessor internalGradientRGB(ImageProcessor image, Strel strel) 
	{
		// extract channels and allocate memory for result
		Map<String, ByteProcessor> channels = ColorImages.mapChannels(image);
		Collection<ImageProcessor> res = new ArrayList<ImageProcessor>(channels.size());
		
		// Process each channel individually
		for (String name : new String[]{"red", "green", "blue"})
		{
			strel.setChannelName(name);
			res.add(internalGradient(channels.get(name), strel));
		}
		
		return ColorImages.mergeChannels(res);
	}

	/** 
	 * Computes the morphological internal gradient of the 3D input image.
	 * The morphological internal gradient is obtained by from the difference 
	 * of original image with the result of an erosion.
	 * 
	 * @see #erosion(ImageStack, Strel3D)
	 * @see #gradient(ImageStack, Strel3D)
	 * @see #externalGradient(ImageStack, Strel3D)
	 * 
	 * @param image
	 *            the input image to process
	 * @param strel
	 *            the structuring element used for morphological internal gradient
	 * @return the result of the 3D morphological internal gradient
	 */
	public static ImageStack internalGradient(ImageStack image, Strel3D strel)
	{
		checkImageType(image);
		
		// First performs erosion
		ImageStack result = strel.erosion(image);
		
		// Determine max possible value from bit depth
		double maxVal = getMaxPossibleValue(image);

		// Compute subtraction of result from original image
		int nx = image.getWidth();
		int ny = image.getHeight();
		int nz = image.getSize();
		for (int z = 0; z < nz; z++) 
		{
			for (int y = 0; y < ny; y++) 
			{
				for (int x = 0; x < nx; x++) 
				{
					double v1 = image.getVoxel(x, y, z);
					double v2 = result.getVoxel(x, y, z);
					result.setVoxel(x, y, z, min(max(v1 - v2, 0), maxVal));
				}
			}
		}
		
		return result;
	}

	/** 
	 * Computes the morphological external gradient of the input image.
	 * The morphological external gradient is obtained by from the difference 
	 * of the result of a dilation and of the original image .
	 * 
	 * @see #dilation(ImageProcessor, Strel)
	 * 
	 * @param image
	 *            the input image to process (grayscale or RGB)
	 * @param strel
	 *            the structuring element used for morphological external gradient
	 * @return the result of the morphological external gradient
	 */
	public static ImageProcessor externalGradient(ImageProcessor image, Strel strel) 
	{
		checkImageType(image);
		if (image instanceof ColorProcessor)
			return externalGradientRGB(image, strel);

		// First performs dilation
		ImageProcessor result = strel.dilation(image);

		// Subtract original image from dilation
		int count = image.getPixelCount();
		if (image instanceof ByteProcessor) 
		{
			for (int i = 0; i < count; i++) 
			{
				// Forces computation using integers, because opening with 
				// octagons can greater than original image (bug)
				int v1 = result.get(i);
				int v2 = image.get(i);
				result.set(i, clamp(v1 - v2, 0, 255));
			}
		} 
		else 
		{
			for (int i = 0; i < count; i++) 
			{
				float v1 = result.getf(i);
				float v2 = image.getf(i);
				result.setf(i, v1 - v2);
			}
		}
		// return gradient
		return result;
	}

	private static ImageProcessor externalGradientRGB(ImageProcessor image, Strel strel)
	{
		// extract channels and allocate memory for result
		Map<String, ByteProcessor> channels = ColorImages.mapChannels(image);
		Collection<ImageProcessor> res = new ArrayList<ImageProcessor>(channels.size());
		
		// Process each channel individually
		for (String name : new String[]{"red", "green", "blue"}) 
		{
			strel.setChannelName(name);
			res.add(externalGradient(channels.get(name), strel));
		}
		
		return ColorImages.mergeChannels(res);
	}

	/** 
	 * Computes the morphological external gradient of the input 3D image.
	 * The morphological external gradient is obtained by from the difference 
	 * of the result of a dilation and of the original image .
	 * 
	 * @see #dilation(ImageStack, Strel3D)
	 * @see #internalGradient(ImageStack, Strel3D)
	 * 
	 * @param image
	 *            the input image to process 
	 * @param strel
	 *            the structuring element used for morphological external gradient
	 * @return the result of the 3D morphological external gradient
	 */
	public static ImageStack externalGradient(ImageStack image, Strel3D strel) 
	{
		checkImageType(image);
		
		// First performs dilation
		ImageStack result = strel.dilation(image);
		
		// Determine max possible value from bit depth
		double maxVal = getMaxPossibleValue(image);
		
		// Compute subtraction of result from original image
		int nx = image.getWidth();
		int ny = image.getHeight();
		int nz = image.getSize();
		for (int z = 0; z < nz; z++)
		{
			for (int y = 0; y < ny; y++) 
			{
				for (int x = 0; x < nx; x++)
				{
					double v1 = result.getVoxel(x, y, z);
					double v2 = image.getVoxel(x, y, z);
					result.setVoxel(x, y, z, min(max(v1 - v2, 0), maxVal));
				}
			}
		}
		
		return result;
	}


	// =======================================================================
	// Private utilitary functions
	
	/**
	 * Check that input image can be processed for classical algorithms, and throw an
	 * exception if not the case.
	 * In the current version, accepts all image types.
	 */
	private final static void checkImageType(ImageProcessor image)
	{
//		if ((image instanceof FloatProcessor)
//				|| (image instanceof ShortProcessor)) {
//			throw new IllegalArgumentException(
//					"Input image must be a ByteProcessor or a ColorProcessor");
//		}
	}

	/**
	 * Check that input image can be processed for classical algorithms, and throw an
	 * exception if not the case.
	 * In the current version, accepts all image types.
	 */
	private final static void checkImageType(ImageStack stack)
	{
//		ImageProcessor image = stack.getProcessor(1);
//		if ((image instanceof FloatProcessor) || (image instanceof ShortProcessor)) {
//			throw new IllegalArgumentException("Input image must be a ByteProcessor or a ColorProcessor");
//		}
	}

	/**
	 * Determine max possible value from bit depth.
	 *  8 bits -> 255
	 * 16 bits -> 65535
	 * 32 bits -> Float.MAX_VALUE
	 */
	private static final double getMaxPossibleValue(ImageStack stack)
	{
		double maxVal = 255;
		int bitDepth = stack.getBitDepth(); 
		if (bitDepth == 16)
		{
			maxVal = 65535;
		}
		else if (bitDepth == 32)
		{
			maxVal = Float.MAX_VALUE;
		}
		return maxVal;
	}
	
	private final static int clamp(int value, int min, int max) 
	{
		return Math.min(Math.max(value, min), max);
	}
}
