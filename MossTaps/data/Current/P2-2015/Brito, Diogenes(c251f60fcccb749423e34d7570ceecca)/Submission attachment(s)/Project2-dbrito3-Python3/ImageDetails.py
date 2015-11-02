from PIL import Image

import sys

this = sys.modules[__name__]

def getNoneMatrix(num_cols, num_rows):
  matrix = [[None for x in range(num_cols)] for x in range(num_cols)]
  return matrix

def getNoneSquareMatrix(num_cols):
  return this.getNoneMatrix(num_cols, num_cols)

def subdivideSquareImage(image, num_cols):
  sectors = this.getNoneSquareMatrix(num_cols)
  width = image.size[0]
  sector_width = width / num_cols

  for row in range(num_cols):
    for col in range(num_cols):
      crop_bounds = (col * width, row * width, col * width + width, row * width + width)
      sectors[col][row] = image.crop(crop_bounds)

  return sectors

def convertToBlackandWhite(image):
  #return image.convert('L').point(lambda x: 0 if x<128 else 255, '1')
  return image.convert('1')

class ImageDetails:
  _image_bw = None
  _image_sectors = None
  _black_px_by_sector = None

  def __init__(self, image, num_sectors_per_dim=5):
    self.image = image
    self.num_sectors_per_dim = num_sectors_per_dim

  @property
  def image_bw(self):
    if not self._image_bw:
      self._image_bw = this.convertToBlackandWhite(self.image)
    return self._image_bw

  @property
  def image_sectors(self):
    if not self._image_sectors:
      self._image_sectors = this.subdivideSquareImage(self.image_bw, self.num_sectors_per_dim)
    return self._image_sectors

  @property
  def black_px_by_sector(self):
    if not self._black_px_by_sector:
      dim = self.num_sectors_per_dim
      black_px_matrix = this.getNoneSquareMatrix(dim)

      for col in range(dim):
        for row in range(dim):
          histogram = self.image_sectors[col][row].histogram()
          black_px_matrix[col][row] = histogram[0]

      self._black_px_by_sector = black_px_matrix

    return self._black_px_by_sector
